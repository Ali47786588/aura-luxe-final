import React, { useState } from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { X, Lock, Eye, EyeOff, ShieldCheck, KeyRound, AlertTriangle } from 'lucide-react';

export const AdminLoginModal: React.FC = () => {
  const { language, loginWithPin, closeModal } = useAuraLuxe();
  const isFa = language === 'FA';

  const [pin, setPin] = useState<string>('');
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [lockoutSeconds, setLockoutSeconds] = useState<number>(0);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!pin.trim()) return;
    const result = loginWithPin(pin);
    if (!result.success) {
      setErrorMessage(result.errorMessage || 'رمز عبور نادرست است.');
      setLockoutSeconds(result.remainingSeconds || 0);
    }
  };

  const setDemoPin = (code: string) => {
    setPin(code);
    setErrorMessage(null);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 overflow-y-auto animate-fadeIn">
      <div className="relative w-full max-w-md bg-[#11131A] border border-[#E5C378]/40 rounded-3xl p-6 sm:p-8 shadow-2xl my-8">
        {/* Close Button */}
        <button
          onClick={closeModal}
          className="absolute top-5 left-5 w-8 h-8 rounded-full bg-[#161822] border border-[#232736] flex items-center justify-center text-[#9CA3AF] hover:text-white hover:bg-[#232736] transition-colors cursor-pointer"
        >
          <X className="w-4 h-4" />
        </button>

        {/* Header Icon */}
        <div className="text-center mb-6">
          <div className="w-14 h-14 rounded-2xl bg-[#E5C378]/15 border border-[#E5C378]/40 text-[#E5C378] flex items-center justify-center mx-auto mb-3 shadow-[0_0_20px_rgba(229,195,120,0.2)]">
            <Lock className="w-7 h-7" />
          </div>

          <h2 className="text-xl font-bold text-white mb-1">
            {isFa ? 'ورود به پرتال مدیریت مراکز' : 'Executive Management Access'}
          </h2>
          <p className="text-xs text-[#9CA3AF]">
            {isFa
              ? 'جهت ورود به پنل، گذرواژه امنیتی مدیریت خود را وارد نمایید.'
              : 'Enter your administrative passcode to access management consoles.'}
          </p>
        </div>

        {/* Demo Fast Fill Pills */}
        <div className="p-3 rounded-2xl bg-[#161822] border border-[#232736] mb-5">
          <div className="text-[11px] text-[#E5C378] font-bold mb-2 flex items-center gap-1.5">
            <KeyRound className="w-3.5 h-3.5" />
            <span>{isFa ? 'گذرواژه‌های حالت ارزیابی آزمایشی (Demo):' : 'Demo Sandbox Credentials:'}</span>
          </div>
          <div className="grid grid-cols-2 gap-2">
            <button
              type="button"
              onClick={() => setDemoPin('2026')}
              className="py-1.5 px-2 rounded-xl bg-[#090A0D] hover:bg-[#232736] border border-[#232736] text-[11px] text-[#F9FAFB] hover:text-[#E5C378] transition-colors cursor-pointer text-right"
            >
              <div className="font-bold">{isFa ? 'مدیر کلینیک' : 'Clinic Director'}</div>
              <div className="font-mono text-[#E5C378] text-[10px]">PIN: 2026</div>
            </button>

            <button
              type="button"
              onClick={() => setDemoPin('7890')}
              className="py-1.5 px-2 rounded-xl bg-[#090A0D] hover:bg-[#232736] border border-[#232736] text-[11px] text-[#F9FAFB] hover:text-[#E5C378] transition-colors cursor-pointer text-right"
            >
              <div className="font-bold">{isFa ? 'سوپرادمین سیستم' : 'SuperAdmin Root'}</div>
              <div className="font-mono text-[#E5C378] text-[10px]">PIN: 7890</div>
            </button>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="text-xs text-[#9CA3AF] block mb-1.5 font-medium">
              {isFa ? 'گذرواژه امنیتی مدیریت:' : 'Management Passcode:'}
            </label>
            <div className="relative">
              <input
                type={showPassword ? 'text' : 'password'}
                maxLength={8}
                value={pin}
                onChange={(e) => {
                  setPin(e.target.value);
                  setErrorMessage(null);
                }}
                placeholder="••••"
                className="w-full px-4 py-3 rounded-xl bg-[#161822] border border-[#232736] text-white font-mono text-center tracking-widest text-lg placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-[#9CA3AF] hover:text-white cursor-pointer"
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          {/* Error Message */}
          {errorMessage && (
            <div className="p-2.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 flex-shrink-0" />
              <span>{errorMessage}</span>
            </div>
          )}

          <p className="text-[10px] text-[#6B7280] leading-relaxed">
            {isFa
              ? 'دسترسی منحصر به مدیران بالینی و مدیران ارشد سیستم تایید شده می‌باشد.'
              : 'Access restricted to verified clinical directors and system administrators.'}
          </p>

          <button
            type="submit"
            disabled={!pin.trim() || lockoutSeconds > 0}
            className="w-full py-3 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-[#090A0D] font-bold text-xs shadow-lg transition-all cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isFa ? 'احراز هویت و ورود به پرتال' : 'Authenticate & Enter'}
          </button>
        </form>
      </div>
    </div>
  );
};
