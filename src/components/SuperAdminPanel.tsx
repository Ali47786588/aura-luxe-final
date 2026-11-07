import React, { useState } from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { EscrowTransaction, OnboardingApplication, SecurityAuditLog } from '../types';
import { formatPrice } from '../utils/localization';
import {
  Shield,
  LogOut,
  CheckCircle2,
  XCircle,
  FileText,
  Clock,
  Layers,
  Sparkles,
  Zap,
  Lock,
  ArrowRight,
  Receipt,
  UserCheck,
  PenTool,
  AlertTriangle,
  Building,
} from 'lucide-react';

export const SuperAdminPanel: React.FC = () => {
  const {
    language,
    applications,
    escrowTransactions,
    auditLogs,
    updateApplicationStatus,
    confirmPatientArrival,
    openDigitalSignaturePad,
    fastForwardGuarantee,
    releaseEscrow,
    openInvoice,
    logout,
  } = useAuraLuxe();

  const isFa = language === 'FA';
  const [selectedTab, setSelectedTab] = useState<'approvals' | 'escrow' | 'audit'>('escrow');

  const pendingApps = applications.filter((a) => a.status === 'PENDING');
  const totalGMVToman = 248600000;
  const totalPlatformCutToman = 19888000;

  return (
    <div className="mx-4 lg:mx-8 py-6 space-y-6">
      {/* Master Console Banner */}
      <div className="p-6 rounded-3xl bg-[#11131A] border border-[#E5C378]/40 shadow-2xl">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
          <div className="flex items-center gap-3.5">
            <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-[#E5C378] to-[#997935] text-[#090A0D] flex items-center justify-center font-bold shadow-lg">
              <Shield className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-[#E5C378] tracking-wider uppercase font-mono">
                  {isFa ? 'پنل حاکمیت مرکزی سوپرادمین' : 'SUPERADMIN MASTER GOVERNANCE'}
                </span>
                <span className="px-2 py-0.5 rounded-full bg-amber-500/20 border border-amber-500/40 text-amber-300 text-[10px] font-mono">
                  ROOT PRIVILEGES
                </span>
              </div>
              <h1 className="text-xl sm:text-2xl font-extrabold text-white">
                {isFa
                  ? 'نظارت عالیه بالینی، تسویه اسکرو و ممیزی مدارک'
                  : 'Master Regulatory & Escrow Engine'}
              </h1>
            </div>
          </div>

          <button
            onClick={logout}
            className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-rose-500/15 border border-rose-500/30 text-rose-400 hover:bg-rose-500/25 text-xs font-semibold transition-all cursor-pointer"
          >
            <LogOut className="w-4 h-4" />
            <span>{isFa ? 'خروج از پنل' : 'Exit SuperAdmin'}</span>
          </button>
        </div>

        {/* Global KPIs */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pt-4 border-t border-[#232736]">
          <div className="p-4 rounded-2xl bg-[#161822] border border-[#232736]">
            <div className="text-xs text-[#9CA3AF] mb-1">
              {isFa ? 'حجم ناخالص معاملات (GMV)' : 'Gross Volume (GMV)'}
            </div>
            <div className="text-xl font-extrabold text-[#E5C378] font-mono">
              {formatPrice(totalGMVToman, language)}
            </div>
          </div>

          <div className="p-4 rounded-2xl bg-[#161822] border border-[#232736]">
            <div className="text-xs text-[#9CA3AF] mb-1">
              {isFa ? 'کارمزد کل پلتفرم (۸٪)' : 'Platform Commission (8%)'}
            </div>
            <div className="text-xl font-extrabold text-emerald-400 font-mono">
              {formatPrice(totalPlatformCutToman, language)}
            </div>
          </div>

          <div className="p-4 rounded-2xl bg-[#161822] border border-[#232736]">
            <div className="text-xs text-[#9CA3AF] mb-1">
              {isFa ? 'مراکز در صف احراز مدارک (KYC)' : 'Partner KYC Queue'}
            </div>
            <div className="text-xl font-extrabold text-amber-400 font-mono">
              {pendingApps.length} {isFa ? 'مرکز' : 'Centers'}
            </div>
          </div>
        </div>
      </div>

      {/* Navigation Sub-tabs */}
      <div className="flex items-center gap-2 p-1.5 rounded-2xl bg-[#11131A] border border-[#232736] max-w-xl">
        <button
          onClick={() => setSelectedTab('escrow')}
          className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-2 ${
            selectedTab === 'escrow'
              ? 'bg-[#E5C378] text-[#090A0D] shadow'
              : 'text-[#9CA3AF] hover:text-white'
          }`}
        >
          <Receipt className="w-4 h-4" />
          <span>{isFa ? 'موتور تسویه اسکرو و قراردادها' : 'Escrow Clearing'}</span>
        </button>

        <button
          onClick={() => setSelectedTab('approvals')}
          className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-2 ${
            selectedTab === 'approvals'
              ? 'bg-[#E5C378] text-[#090A0D] shadow'
              : 'text-[#9CA3AF] hover:text-white'
          }`}
        >
          <Building className="w-4 h-4" />
          <span>{isFa ? `تایید مدارک مراکز (${pendingApps.length})` : `KYC Approvals (${pendingApps.length})`}</span>
        </button>

        <button
          onClick={() => setSelectedTab('audit')}
          className={`flex-1 py-2.5 rounded-xl text-xs font-bold transition-all cursor-pointer flex items-center justify-center gap-2 ${
            selectedTab === 'audit'
              ? 'bg-[#E5C378] text-[#090A0D] shadow'
              : 'text-[#9CA3AF] hover:text-white'
          }`}
        >
          <Lock className="w-4 h-4" />
          <span>{isFa ? 'لاگ‌های ممیزی امنیتی' : 'Audit Logs'}</span>
        </button>
      </div>

      {/* TAB CONTENT 1: ESCROW ENGINE */}
      {selectedTab === 'escrow' && (
        <div className="space-y-4">
          <div className="p-4 rounded-2xl bg-[#11131A] border border-[#232736] text-xs text-[#9CA3AF] flex items-center justify-between">
            <span>
              {isFa
                ? 'چرخه ۴ مرحله‌ای اسکرو: ۱. واریز و نگهداری ➔ ۲. ورود و احراز هویت بیمار ➔ ۳. امضای اتمام درمان ➔ ۴. پنجره تضمین ۱۲۰ دقیقه‌ای و تسویه به شبا'
                : 'Regulated 4-step escrow pipeline: 1. Held in Escrow -> 2. Patient Arrival -> 3. Sign-off -> 4. 120m Cooldown -> Sheba Release'}
            </span>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
            {escrowTransactions.map((tx) => (
              <EscrowCard
                key={tx.id}
                tx={tx}
                isFa={isFa}
                language={language}
                onConfirmArrival={() => confirmPatientArrival(tx.id)}
                onOpenSignature={() => openDigitalSignaturePad(tx)}
                onFastForward={() => fastForwardGuarantee(tx.id)}
                onRelease={() => releaseEscrow(tx.id)}
                onViewInvoice={() => openInvoice(tx)}
              />
            ))}
          </div>
        </div>
      )}

      {/* TAB CONTENT 2: KYC APPROVALS */}
      {selectedTab === 'approvals' && (
        <div className="p-6 rounded-3xl bg-[#11131A] border border-[#232736] space-y-4">
          <h2 className="text-base font-bold text-white mb-2">
            {isFa ? 'درخواست‌های همکاری مراکز و کلینیک‌های متقاضی' : 'Partner Facilities Licensing Applications'}
          </h2>

          <div className="space-y-4">
            {applications.map((app) => (
              <div
                key={app.id}
                className="p-5 rounded-2xl bg-[#161822] border border-[#232736] flex flex-col md:flex-row md:items-center justify-between gap-4"
              >
                <div className="space-y-1.5">
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-white text-sm">{app.businessName}</span>
                    <span
                      className={`px-2 py-0.5 rounded-full text-[10px] font-bold font-mono ${
                        app.status === 'APPROVED'
                          ? 'bg-emerald-500/20 text-emerald-300'
                          : app.status === 'REJECTED'
                          ? 'bg-rose-500/20 text-rose-300'
                          : 'bg-amber-500/20 text-amber-300'
                      }`}
                    >
                      {app.status}
                    </span>
                  </div>

                  <div className="text-xs text-[#9CA3AF] flex flex-wrap gap-x-4 gap-y-1">
                    <span>
                      {isFa ? 'متقاضی:' : 'Applicant:'} {app.applicantName}
                    </span>
                    <span>
                      {isFa ? 'شماره تماس:' : 'Phone:'} {app.phoneNumber}
                    </span>
                    <span>
                      {isFa ? 'شماره نظام/صنف:' : 'License:'} {app.medicalCouncilOrGuildId}
                    </span>
                    <span>
                      {isFa ? 'تعداد یونیت:' : 'Units:'} {app.chairsOrBedsCount}
                    </span>
                  </div>

                  <p className="text-xs text-[#E5C378]">{app.shortDescription}</p>
                  <p className="text-[11px] text-[#6B7280] font-mono">
                    {isFa ? 'شماره شبا مقصد:' : 'Settlement IBAN:'} {app.shebaNumber}
                  </p>
                </div>

                {app.status === 'PENDING' && (
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => updateApplicationStatus(app.id, 'APPROVED')}
                      className="px-4 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-black font-bold text-xs transition-colors cursor-pointer"
                    >
                      {isFa ? 'تایید و صدور پروانه' : 'Approve'}
                    </button>
                    <button
                      onClick={() => updateApplicationStatus(app.id, 'REJECTED')}
                      className="px-4 py-2 rounded-xl bg-rose-500/20 hover:bg-rose-500/30 text-rose-400 font-bold text-xs transition-colors cursor-pointer"
                    >
                      {isFa ? 'رد درخواست' : 'Decline'}
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* TAB CONTENT 3: AUDIT LOGS */}
      {selectedTab === 'audit' && (
        <div className="p-6 rounded-3xl bg-[#11131A] border border-[#232736] space-y-4">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-base font-bold text-white">
              {isFa ? 'دفتر ثبت وقایع و ممیزی امنیتی (تغییرناپذیر)' : 'Immutable Security & Audit Ledger'}
            </h2>
            <span className="text-xs text-[#9CA3AF] font-mono">
              {auditLogs.length} Records Logged
            </span>
          </div>

          <div className="space-y-3 max-h-[600px] overflow-y-auto pr-1">
            {auditLogs.map((log) => (
              <div
                key={log.id}
                className="p-3.5 rounded-xl bg-[#161822] border border-[#232736] flex flex-col sm:flex-row sm:items-center justify-between gap-2"
              >
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <span
                      className={`px-2 py-0.5 rounded text-[10px] font-bold font-mono ${
                        log.severity === 'CRITICAL'
                          ? 'bg-rose-500/20 text-rose-400'
                          : log.severity === 'WARNING'
                          ? 'bg-amber-500/20 text-amber-400'
                          : 'bg-sky-500/20 text-sky-400'
                      }`}
                    >
                      {log.severity}
                    </span>
                    <span className="text-xs font-bold text-white">
                      {isFa ? log.titleFa : log.titleEn}
                    </span>
                  </div>
                  <p className="text-xs text-[#9CA3AF]">{log.details}</p>
                </div>

                <div className="text-right text-[11px] text-[#6B7280] font-mono flex-shrink-0">
                  <div>{log.timestamp}</div>
                  <div>IP: {log.ip}</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

interface EscrowCardProps {
  tx: EscrowTransaction;
  isFa: boolean;
  language: 'FA' | 'EN';
  onConfirmArrival: () => void;
  onOpenSignature: () => void;
  onFastForward: () => void;
  onRelease: () => void;
  onViewInvoice: () => void;
}

const EscrowCard: React.FC<EscrowCardProps> = ({
  tx,
  isFa,
  language,
  onConfirmArrival,
  onOpenSignature,
  onFastForward,
  onRelease,
  onViewInvoice,
}) => {
  const steps = [
    { num: 1, labelFa: 'واریز شاپرک', labelEn: 'Held' },
    { num: 2, labelFa: 'ورود بیمار', labelEn: 'Arrived' },
    { num: 3, labelFa: 'اتمام و امضا', labelEn: 'Done' },
    { num: 4, labelFa: 'تسویه شبا', labelEn: 'Settled' },
  ];

  return (
    <div className="p-5 rounded-3xl bg-[#11131A] border border-[#232736] space-y-4 flex flex-col justify-between">
      <div>
        {/* Contract Code & Invoice Trigger */}
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold font-mono text-[#E5C378]">{tx.contractCode}</span>
            <span className="text-[10px] text-[#9CA3AF] font-mono">RRN: {tx.paymentRefId}</span>
          </div>

          <button
            onClick={onViewInvoice}
            className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-[#161822] hover:bg-[#232736] text-[11px] text-white border border-[#232736] cursor-pointer"
          >
            <FileText className="w-3.5 h-3.5 text-[#E5C378]" />
            <span>{isFa ? 'صورتحساب رسمی' : 'Tax Invoice'}</span>
          </button>
        </div>

        {/* Practitioner & Service */}
        <h3 className="text-base font-bold text-white mb-0.5">{tx.practitionerName}</h3>
        <p className="text-xs text-[#E5C378] mb-3">
          {tx.venueName} • {tx.slotName}
        </p>

        {/* Milestone Steps Bar */}
        <div className="grid grid-cols-4 gap-1.5 p-1 rounded-xl bg-[#090A0D] border border-[#232736] mb-4">
          {steps.map((st) => {
            const isDone = tx.milestoneStep >= st.num;
            const isCurrent = tx.milestoneStep === st.num;
            return (
              <div
                key={st.num}
                className={`py-1.5 text-center rounded-lg text-[10px] font-bold ${
                  isDone
                    ? 'bg-[#E5C378] text-[#090A0D]'
                    : isCurrent
                    ? 'bg-white/10 text-white'
                    : 'text-[#6B7280]'
                }`}
              >
                {st.num}. {isFa ? st.labelFa : st.labelEn}
              </div>
            );
          })}
        </div>

        {/* Financial Breakdown */}
        <div className="grid grid-cols-3 gap-2 p-3 rounded-xl bg-[#161822] border border-[#232736] text-xs mb-3">
          <div>
            <span className="text-[10px] text-[#9CA3AF] block">{isFa ? 'مبلغ کل:' : 'Gross:'}</span>
            <span className="font-bold text-white font-mono">
              {formatPrice(tx.grossAmountToman, language)}
            </span>
          </div>
          <div>
            <span className="text-[10px] text-[#9CA3AF] block">
              {isFa ? 'کارمزد (۸٪):' : 'Fee (8%):'}
            </span>
            <span className="font-bold text-rose-400 font-mono">
              {formatPrice(tx.platformFeeToman, language)}
            </span>
          </div>
          <div>
            <span className="text-[10px] text-[#9CA3AF] block">
              {isFa ? 'خالص تسویه:' : 'Net Payout:'}
            </span>
            <span className="font-bold text-emerald-400 font-mono">
              {formatPrice(tx.clinicPayoutToman, language)}
            </span>
          </div>
        </div>

        {/* Verification Meta */}
        <div className="text-[11px] text-[#9CA3AF] space-y-1 mb-2">
          <div>
            {isFa ? 'کد ملی بیمار:' : 'Patient National ID:'}{' '}
            <span className="font-mono text-white">{tx.patientNationalId}</span>{' '}
            {tx.patientNationalIdVerified && (
              <span className="text-emerald-400 font-bold">({isFa ? 'احراز شده' : 'Verified'})</span>
            )}
          </div>
          <div>
            {isFa ? 'شماره شبا مقصد:' : 'Sheba IBAN:'}{' '}
            <span className="font-mono text-white text-[10px]">{tx.shebaNumber}</span>
          </div>
          <div className="text-[#E5C378] italic">{tx.estimatedReleaseTime}</div>
        </div>
      </div>

      {/* Action Triggers depending on Current Step */}
      <div className="pt-3 border-t border-[#232736]">
        {tx.milestoneStep === 1 && (
          <button
            onClick={onConfirmArrival}
            className="w-full py-2.5 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] text-[#090A0D] font-bold text-xs flex items-center justify-center gap-1.5 shadow cursor-pointer active:scale-98"
          >
            <UserCheck className="w-4 h-4" />
            <span>{isFa ? 'احراز هویت بیمار و ثبت ورود' : 'Verify Patient Arrival'}</span>
          </button>
        )}

        {tx.milestoneStep === 2 && (
          <button
            onClick={onOpenSignature}
            className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-black font-bold text-xs flex items-center justify-center gap-1.5 shadow cursor-pointer active:scale-98"
          >
            <PenTool className="w-4 h-4" />
            <span>{isFa ? 'امضای دیجیتال و ترخیص پزشک' : 'Digital Doctor Sign-Off'}</span>
          </button>
        )}

        {tx.milestoneStep === 3 && (
          <div className="space-y-2">
            {tx.guaranteeMinutesRemaining > 0 ? (
              <div className="flex items-center gap-2">
                <div className="flex-1 text-[11px] text-amber-400 flex items-center gap-1.5 bg-amber-500/10 p-2 rounded-xl border border-amber-500/20">
                  <Clock className="w-4 h-4" />
                  <span>
                    {isFa
                      ? `${tx.guaranteeMinutesRemaining} دقیقه از پنجره تضمین باقیست`
                      : `${tx.guaranteeMinutesRemaining}m guarantee remaining`}
                  </span>
                </div>
                <button
                  onClick={onFastForward}
                  className="px-3 py-2 rounded-xl bg-amber-500/20 hover:bg-amber-500/30 text-amber-300 font-bold text-xs cursor-pointer"
                  title="Fast-forward cooldown (Demo)"
                >
                  {isFa ? 'پایان زمان (تست)' : 'Skip Timer'}
                </button>
              </div>
            ) : (
              <button
                onClick={onRelease}
                className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-black font-bold text-xs flex items-center justify-center gap-1.5 shadow cursor-pointer active:scale-98"
              >
                <Zap className="w-4 h-4" />
                <span>{isFa ? 'تسویه نهایی به شماره شبا بانک' : 'Release Escrow to IBAN'}</span>
              </button>
            )}
          </div>
        )}

        {tx.milestoneStep === 4 && (
          <div className="text-center py-1 text-xs text-emerald-400 font-semibold flex items-center justify-center gap-1.5">
            <CheckCircle2 className="w-4 h-4" />
            <span>{isFa ? 'تسویه کامل به شماره شبا انجام گردید' : 'Settled to Bank IBAN'}</span>
          </div>
        )}
      </div>
    </div>
  );
};
