import React from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { EscrowTransaction } from '../../types';
import { formatPrice } from '../../utils/localization';
import { X, Receipt, Shield, Printer, CheckCircle2, Building2 } from 'lucide-react';

export const InvoiceDialog: React.FC = () => {
  const {
    language,
    selectedInvoiceTx,
    closeModal,
  } = useAuraLuxe();

  const isFa = language === 'FA';
  const tx = selectedInvoiceTx;

  if (!tx) return null;

  const vatToman = Math.round(tx.platformFeeToman * 0.09);

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/85 backdrop-blur-sm p-4 overflow-y-auto animate-fadeIn">
      <div className="relative w-full max-w-xl bg-[#11131A] border border-[#E5C378]/40 rounded-3xl p-6 sm:p-8 shadow-2xl my-8">
        {/* Close Button */}
        <button
          onClick={closeModal}
          className="absolute top-5 left-5 w-8 h-8 rounded-full bg-[#161822] border border-[#232736] flex items-center justify-center text-[#9CA3AF] hover:text-white hover:bg-[#232736] transition-colors cursor-pointer"
        >
          <X className="w-4 h-4" />
        </button>

        {/* Invoice Header */}
        <div className="pb-5 border-b border-[#232736] mb-5">
          <div className="flex items-center gap-1.5 text-xs text-[#E5C378] font-mono uppercase mb-1">
            <Receipt className="w-3.5 h-3.5" />
            <span>AURA LUXE ESCROW CLEARING HOUSE</span>
          </div>
          <h2 className="text-xl font-bold text-white">
            {isFa ? 'صورتحساب رسمی تسویه مالیاتی و اسکرو (B2B)' : 'Official B2B Tax & Clearing Invoice'}
          </h2>
          <div className="flex items-center gap-3 text-xs text-[#9CA3AF] font-mono mt-1">
            <span>شماره قرارداد: {tx.contractCode}</span>
            <span>•</span>
            <span>کد پیگیری شاپرک: {tx.paymentRefId}</span>
          </div>
        </div>

        {/* Parties Details */}
        <div className="grid grid-cols-2 gap-4 p-4 rounded-2xl bg-[#161822] border border-[#232736] text-xs mb-5">
          <div>
            <span className="text-[10px] text-[#9CA3AF] block mb-0.5">
              {isFa ? 'پزشک / مجری درمان:' : 'Practitioner / Surgeon:'}
            </span>
            <span className="font-bold text-white block">{tx.practitionerName}</span>
            <span className="text-[10px] text-[#E5C378] font-mono">{tx.licenseId}</span>
          </div>

          <div>
            <span className="text-[10px] text-[#9CA3AF] block mb-0.5">
              {isFa ? 'مرکز و سوئیت بالینی:' : 'Facility & Suite:'}
            </span>
            <span className="font-bold text-white block">{tx.venueName}</span>
            <span className="text-[10px] text-[#9CA3AF]">{tx.slotName}</span>
          </div>

          <div>
            <span className="text-[10px] text-[#9CA3AF] block mb-0.5">
              {isFa ? 'کد ملی احراز شده بیمار:' : 'Patient National ID:'}
            </span>
            <span className="font-mono text-white">{tx.patientNationalId}</span>
          </div>

          <div>
            <span className="text-[10px] text-[#9CA3AF] block mb-0.5">
              {isFa ? 'شماره شبا واریز پایا:' : 'Disbursement IBAN:'}
            </span>
            <span className="font-mono text-white text-[10px]">{tx.shebaNumber}</span>
          </div>
        </div>

        {/* Invoice Item Table */}
        <div className="rounded-2xl border border-[#232736] overflow-hidden mb-5 text-xs">
          <div className="bg-[#090A0D] p-3 text-[11px] font-bold text-[#9CA3AF] grid grid-cols-3 border-b border-[#232736]">
            <span className="col-span-2">{isFa ? 'شرح ردیف صورتحساب' : 'Description'}</span>
            <span className="text-left font-mono">{isFa ? 'مبلغ (تومان)' : 'Amount (Toman)'}</span>
          </div>

          <div className="p-3 bg-[#161822] border-b border-[#232736] grid grid-cols-3">
            <span className="col-span-2 text-white">
              {isFa ? 'اجاره سوئیت جراحی / درمانی و تجهیزات' : 'Operatory Suite & Technology Lease'}
            </span>
            <span className="text-left font-mono text-white">
              {formatPrice(tx.grossAmountToman, language)}
            </span>
          </div>

          <div className="p-3 bg-[#161822] border-b border-[#232736] grid grid-cols-3">
            <span className="col-span-2 text-[#9CA3AF]">
              {isFa ? 'کارمزد خدمات نرم‌افزاری و اسکرو پلتفرم (۸٪)' : 'Aura Platform Escrow Fee (8%)'}
            </span>
            <span className="text-left font-mono text-rose-400">
              - {formatPrice(tx.platformFeeToman, language)}
            </span>
          </div>

          <div className="p-3 bg-[#161822] border-b border-[#232736] grid grid-cols-3">
            <span className="col-span-2 text-[#9CA3AF]">
              {isFa ? 'مالیات بر ارزش افزوده خدمات پلتفرم (۹٪)' : 'VAT on Platform Commission (9%)'}
            </span>
            <span className="text-left font-mono text-amber-400">
              {formatPrice(vatToman, language)}
            </span>
          </div>

          <div className="p-3.5 bg-[#090A0D] grid grid-cols-3 items-center">
            <span className="col-span-2 font-bold text-[#E5C378]">
              {isFa ? 'خالص مبلغ واریزی به شبا کلینیک:' : 'Net Disbursement to IBAN:'}
            </span>
            <span className="text-left font-mono font-extrabold text-emerald-400 text-sm">
              {formatPrice(tx.clinicPayoutToman, language)}
            </span>
          </div>
        </div>

        {/* Seal Badge & Print Action */}
        <div className="flex items-center justify-between gap-4 pt-2">
          <div className="flex items-center gap-2 text-xs text-[#10B981]">
            <CheckCircle2 className="w-4 h-4" />
            <span>{isFa ? 'تاییدیه رسمی ممهور به مهر دیجیتال شاپرک' : 'Official Electronic Clearing Seal'}</span>
          </div>

          <button
            onClick={handlePrint}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-[#E5C378] hover:bg-[#FBF4E2] text-[#090A0D] font-bold text-xs transition-colors cursor-pointer"
          >
            <Printer className="w-4 h-4" />
            <span>{isFa ? 'چاپ فاکتور' : 'Print Invoice'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
