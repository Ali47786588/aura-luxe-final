# AURA LUXE — Ultra-Modern B2B Clinical Marketplace

A luxury B2B marketplace and live suite allocation system for board-certified aesthetic clinics, dermatologists, dental smile design studios, and VIP salons in Tehran and Karaj, with full Persian RTL and English localization.

## Features Ported

1. **Public Luxury Marketplace & Catalog**:
   - Curated clinic suites, operatories, and styling lounges across Tehran (Saadat Abad, Elahieh, Zafaraniyeh, Niavaran, Shahrak-e Gharb) and Karaj (Gohardasht, Fardis, Mehrshahr, Azimiyeh).
   - Category filtering (Aesthetics & Laser, Dental Studios, VIP Salons).
   - Deep inspection modal with full technical inventory, sterilization certifications (ISO 13485 cleanroom, Ministry of Health Grade A+), and licensing numbers.

2. **Real-time Operatory & Suite Telemetry**:
   - Live status tracking (Available, In-Procedure, Sanitizing, Reserved).
   - Autoclave pack and assistant/nurse inclusion indicators.
   - 1-click suite reservation workflow.

3. **Cinematic Before / After Outcomes**:
   - Dual-layer interactive draggable comparison slider with clinical filters and outcome metrics.

4. **Clinical Stories & Instagram Highlights**:
   - Multi-slide case highlights with auto-advancing progress timers, slide navigation, and instant doctor inquiry messenger.

5. **Executive Clinic Director Portal**:
   - Authenticated with Demo PIN (`2026`).
   - Real-time unit state toggles (Available / In Procedure / Sanitizing / Reserved).
   - Booking requests review queue (Confirm or Decline).
   - Facility occupancy rate and shift revenue projections.

6. **SuperAdmin Regulatory & Escrow Governance Console**:
   - Authenticated with Demo PIN (`7890`).
   - 4-step escrow clearing engine (Held in Escrow -> Patient Arrival -> Doctor Digital Sign-off -> 120m Cooldown -> Sheba Release).
   - Strict Iranian National ID (Modulo 11) and Sheba IBAN (ISO 7064) checksum validation.
   - Digital signature pad with cryptographic SHA-256 seal.
   - Official B2B tax invoice generator with Shaparak RRN and VAT computation.
   - Partner facility licensing application approvals.
   - Immutable security and audit logging ledger with anti-brute-force lockout.

## Running Locally

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start the development server on port 3000:
   ```bash
   npm run dev
   ```

3. Build for production:
   ```bash
   npm run build
   ```
