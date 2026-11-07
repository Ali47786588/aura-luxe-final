import { UserRole } from '../types';

export interface AuthResult {
  success: boolean;
  role?: UserRole | null;
  token?: string | null;
  isLockedOut?: boolean;
  remainingSeconds?: number;
  errorMessage?: string | null;
  isDemoAuthentication?: boolean;
}

interface ActiveSession {
  token: string;
  role: UserRole;
  createdAtMs: number;
  lastActivityMs: number;
}

class SecurityManagerClass {
  readonly IS_DEMO_MODE = true;
  readonly IS_REAL_FINANCIAL_OPERATIONS_ENABLED = false;
  readonly IS_REAL_ESCROW_RELEASE_ENABLED = false;

  private failedAttempts = 0;
  private lockoutUntilTimeMs = 0;
  private activeSessions = new Map<string, ActiveSession>();
  private readonly SESSION_TTL_MS = 30 * 60 * 1000; // 30 minutes

  authenticate(pin: string): AuthResult {
    const currentTime = Date.now();
    if (this.lockoutUntilTimeMs > currentTime) {
      const remaining = Math.ceil((this.lockoutUntilTimeMs - currentTime) / 1000);
      return {
        success: false,
        isLockedOut: true,
        remainingSeconds: remaining,
        errorMessage: `سرویس آزمایشی مسدود است. لطفاً ${remaining} ثانیه منتظر بمانید.`,
        isDemoAuthentication: true,
      };
    }

    const cleanPin = pin.trim();
    if (cleanPin.length < 4 || cleanPin.length > 16) {
      return this.recordFailedAttempt(currentTime, 'طول گذرواژه وارد شده نامعتبر است.');
    }

    // Supported Demo PINs:
    // 7890 -> SUPER_ADMIN
    // 2026 -> CLINIC_ADMIN
    let targetRole: UserRole | null = null;
    if (cleanPin === '7890') {
      targetRole = 'SUPER_ADMIN';
    } else if (cleanPin === '2026') {
      targetRole = 'CLINIC_ADMIN';
    }

    if (targetRole) {
      this.failedAttempts = 0;
      this.lockoutUntilTimeMs = 0;
      const token = this.generateSessionToken(targetRole);
      this.activeSessions.set(token, {
        token,
        role: targetRole,
        createdAtMs: currentTime,
        lastActivityMs: currentTime,
      });

      return {
        success: true,
        role: targetRole,
        token,
        isDemoAuthentication: true,
      };
    } else {
      return this.recordFailedAttempt(currentTime);
    }
  }

  private recordFailedAttempt(currentTime: number, customMessage?: string): AuthResult {
    this.failedAttempts += 1;
    if (this.failedAttempts >= 3) {
      this.lockoutUntilTimeMs = currentTime + 60000; // 60 seconds
      return {
        success: false,
        isLockedOut: true,
        remainingSeconds: 60,
        errorMessage: '۳ تلاش ناموفق پیاپی. قفل امنیتی به مدت ۶۰ ثانیه فعال گردید.',
        isDemoAuthentication: true,
      };
    } else {
      const attemptsLeft = 3 - this.failedAttempts;
      return {
        success: false,
        isLockedOut: false,
        errorMessage: customMessage || `رمز عبور نادرست است. ${attemptsLeft} تلاش باقی‌مانده. (کدهای دمو: 7890 سوپرادمین، 2026 مدیر کلینیک)`,
        isDemoAuthentication: true,
      };
    }
  }

  isValidSession(token: string | null | undefined, requiredRole: UserRole): boolean {
    if (!token) return false;
    const session = this.activeSessions.get(token);
    if (!session) return false;

    const now = Date.now();
    if (now - session.lastActivityMs > this.SESSION_TTL_MS) {
      this.activeSessions.delete(token);
      return false;
    }

    const hasPermission = session.role === 'SUPER_ADMIN' || session.role === requiredRole;
    if (!hasPermission) return false;

    session.lastActivityMs = now;
    return true;
  }

  invalidateSession(token: string | null | undefined): void {
    if (token) {
      this.activeSessions.delete(token);
    }
  }

  private generateSessionToken(role: UserRole): string {
    const randomHex = Array.from({ length: 32 }, () =>
      Math.floor(Math.random() * 16).toString(16)
    ).join('');
    return `aura_demo_${role.toLowerCase()}_${randomHex}`;
  }

  /**
   * Official Iranian National ID (کد ملی) Checksum Algorithm
   * Verifies 10-digit number modulo 11 checksum.
   */
  isValidNationalId(nationalId: string, strictChecksum = true): boolean {
    const clean = nationalId.trim().replace(/\s/g, '');
    if (!/^\d{10}$/.test(clean)) return false;

    // Reject all identical digits e.g. 0000000000, 1111111111
    if (/^(\d)\1{9}$/.test(clean)) return false;

    if (!strictChecksum) return true;

    let sum = 0;
    for (let i = 0; i < 9; i++) {
      sum += parseInt(clean[i], 10) * (10 - i);
    }

    const remainder = sum % 11;
    const checkDigit = parseInt(clean[9], 10);

    return remainder < 2 ? checkDigit === remainder : checkDigit === (11 - remainder);
  }

  /**
   * ISO 7064 MOD 97-10 Iranian Sheba (IBAN) Checksum Validator
   * Format: IR + 24 numeric digits = 26 alphanumeric chars.
   */
  isValidShebaNumber(sheba: string, strictChecksum = true): boolean {
    const clean = sheba.trim().toUpperCase().replace(/\s/g, '');
    if (!/^IR\d{24}$/.test(clean)) return false;
    if (!strictChecksum) return true;

    const numericString = clean.substring(4) + '1827' + clean.substring(2, 4);
    try {
      const bigInt = BigInt(numericString);
      return bigInt % 97n === 1n;
    } catch {
      return false;
    }
  }

  /**
   * Sanitizes inputs to prevent script execution or tag injection
   */
  sanitizeInput(input: string): string {
    return input
      .trim()
      .replace(/<script.*?>.*?<\/script>/gi, '')
      .replace(/[<>&"']/g, '')
      .slice(0, 500);
  }
}

export const SecurityManager = new SecurityManagerClass();
