import React, { useState } from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { OnboardingApplication } from '../../types';
import { SecurityManager } from '../../utils/security';
import { X, Building2, UserPlus, CheckCircle2, Shield, AlertCircle } from 'lucide-react';

export const OnboardingModal: React.FC = () => {
  const { language, submitOnboarding, closeModal, showToast } = useAuraLuxe();
  const isFa = language === 'FA';

  const [applicantType, setApplicantType] = useState<'clinic' | 'doctor' | 'salon'>('clinic');
  const [businessName, setBusinessName] = useState<string>('');
  const [applicantName, setApplicantName] = useState<string>('');
  const [phoneNumber, setPhoneNumber] = useState<string>('');
  const [cityAndDistrict, setCityAndDistrict] = useState<string>('تهران - سعادت‌آباد');
  const [chairsCount, setChairsCount] = useState<number>(4);
  const [medicalId, setMedicalId] = useState<string>('');
  const [shebaNumber, setShebaNumber] = useState<string>('IR910120000000001829304192');
  const [description, setDescription] = useState<string>('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!businessName.trim() || !applicantName.trim() || !phoneNumber.trim()) return;

    const newApp: OnboardingApplication = {
      id: `app-${Date.now()}`,
      applicantType,
      activityTypeFa:
        applicantType === 'clinic'
          ? 'کلینیک پوست و لیزر'
          : applicantType === 'doctor'
          ? 'پزشک متخصص مستقل'
          : 'سالن زیبایی ممتاز',
      businessName: SecurityManager.sanitizeInput(businessName),
      applicantName: SecurityManager.sanitizeInput(applicantName),
      phoneNumber: SecurityManager.sanitizeInput(phoneNumber),
      cityAndDistrict: SecurityManager.sanitizeInput(cityAndDistrict),
      chairsOrBedsCount: Number(chairsCount) || 1,
      shortDescription: SecurityManager.sanitizeInput(description),
      medicalCouncilOrGuildId: SecurityManager.sanitizeInput(medicalId) || 'PENDING-VERIFY',
      shebaNumber: shebaNumber.trim().toUpperCase(),
      status: 'PENDING',
      submittedAt: 'هم‌اکنون',
    };

    submitOnboarding(newApp);
    closeModal();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 overflow-y-auto animate-fadeIn">
      <div className="relative w-full max-w-xl bg-[#11131A] border border-[#E5C378]/40 rounded-3xl p-6 sm:p-8 shadow-2xl my-8">
        {/* Close Button */}
        <button
          onClick={closeModal}
          className="absolute top-5 left-5 w-8 h-8 rounded-full bg-[#161822] border border-[#232736] flex items-center justify-center text-[#9CA3AF] hover:text-white hover:bg-[#232736] transition-colors cursor-pointer"
        >
          <X className="w-4 h-4" />
        </button>

        {/* Modal Header */}
        <div className="mb-6">
          <div className="flex items-center gap-1.5 text-xs text-[#E5C378] font-mono uppercase mb-1">
            <UserPlus className="w-3.5 h-3.5" />
            <span>{isFa ? 'پذیرش مراکز و پزشکان' : 'PARTNER REGISTRATION'}</span>
          </div>
          <h2 className="text-xl font-bold text-white">
            {isFa ? 'درخواست اتصال کلینیک و مرکز به شبکه اورا لوکس' : 'Partner Facility Application'}
          </h2>
          <p className="text-xs text-[#9CA3AF] mt-1">
            {isFa
              ? 'سوئیت‌های بالینی و فضاهای اپراتوری خود را جهت اشتراک‌گذاری با متخصصین ثبت نمایید.'
              : 'List your idle operatories, surgical suites, and laser cabines to verified practitioners.'}
          </p>
        </div>

        {/* Applicant Type Segmented Control */}
        <div className="grid grid-cols-3 gap-2 p-1 rounded-2xl bg-[#090A0D] border border-[#232736] mb-5">
          <button
            type="button"
            onClick={() => setApplicantType('clinic')}
            className={`py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
              applicantType === 'clinic'
                ? 'bg-[#E5C378] text-[#090A0D] shadow'
                : 'text-[#9CA3AF] hover:text-white'
            }`}
          >
            {isFa ? 'کلینیک و مرکز جراحی' : 'Clinic / Center'}
          </button>
          <button
            type="button"
            onClick={() => setApplicantType('doctor')}
            className={`py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
              applicantType === 'doctor'
                ? 'bg-[#E5C378] text-[#090A0D] shadow'
                : 'text-[#9CA3AF] hover:text-white'
            }`}
          >
            {isFa ? 'پزشک متخصص' : 'Practitioner'}
          </button>
          <button
            type="button"
            onClick={() => setApplicantType('salon')}
            className={`py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
              applicantType === 'salon'
                ? 'bg-[#E5C378] text-[#090A0D] shadow'
                : 'text-[#9CA3AF] hover:text-white'
            }`}
          >
            {isFa ? 'سالن زیبایی ممتاز' : 'Luxury Salon'}
          </button>
        </div>

        {/* Application Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="text-[11px] text-[#9CA3AF] block mb-1">
                {isFa ? 'نام تجاری مرکز / کلینیک:' : 'Business / Facility Name:'}
              </label>
              <input
                type="text"
                required
                value={businessName}
                onChange={(e) => setBusinessName(e.target.value)}
                placeholder={isFa ? 'کلینیک درماتولوژی آوا' : 'Ava Aesthetics'}
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
              />
            </div>

            <div>
              <label className="text-[11px] text-[#9CA3AF] block mb-1">
                {isFa ? 'نام مدیر مسئول / پزشک:' : 'Applicant / Director Name:'}
              </label>
              <input
                type="text"
                required
                value={applicantName}
                onChange={(e) => setApplicantName(e.target.value)}
                placeholder={isFa ? 'دکتر مهران شمس' : 'Dr. Mehran Shams'}
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="text-[11px] text-[#9CA3AF] block mb-1">
                {isFa ? 'شماره تماس مستقیم:' : 'Phone Number:'}
              </label>
              <input
                type="tel"
                required
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                placeholder="09121234567"
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white font-mono placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
              />
            </div>

            <div>
              <label className="text-[11px] text-[#9CA3AF] block mb-1">
                {isFa ? 'شهر و منطقه استقرار:' : 'City & District:'}
              </label>
              <input
                type="text"
                required
                value={cityAndDistrict}
                onChange={(e) => setCityAndDistrict(e.target.value)}
                placeholder={isFa ? 'تهران - سعادت‌آباد' : 'Tehran - Saadat Abad'}
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div>
              <label className="text-[11px] text-[#9CA3AF] block mb-1">
                {isFa ? 'شماره نظام پزشکی یا شناسه صنفی:' : 'Medical Council / Guild ID:'}
              </label>
              <input
                type="text"
                value={medicalId}
                onChange={(e) => setMedicalId(e.target.value)}
                placeholder="MED-148201"
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white font-mono placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
              />
            </div>

            <div>
              <label className="text-[11px] text-[#9CA3AF] block mb-1">
                {isFa ? 'تعداد تخت‌ها و صندلی‌های فعال:' : 'Active Chairs / Beds Count:'}
              </label>
              <input
                type="number"
                min={1}
                max={50}
                value={chairsCount}
                onChange={(e) => setChairsCount(Number(e.target.value))}
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white font-mono focus:outline-none focus:border-[#E5C378]"
              />
            </div>
          </div>

          <div>
            <label className="text-[11px] text-[#9CA3AF] block mb-1">
              {isFa ? 'شماره شبا رسمی جهت تسویه اسکرو:' : 'Settlement Sheba (IBAN):'}
            </label>
            <input
              type="text"
              required
              value={shebaNumber}
              onChange={(e) => setShebaNumber(e.target.value)}
              placeholder="IR910120000000001829304192"
              className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white font-mono placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
            />
          </div>

          <div>
            <label className="text-[11px] text-[#9CA3AF] block mb-1">
              {isFa ? 'تجهیزات کلیدی و مشخصات فضا:' : 'Key Equipment & Space Specs:'}
            </label>
            <textarea
              rows={2}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder={isFa ? 'دارای دستگاه کندلا، اتوکلاو کلاس B، پارکینگ ولت...' : 'Candela laser, CBCT scan, valet...'}
              className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
            />
          </div>

          <button
            type="submit"
            className="w-full py-3 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-[#090A0D] font-bold text-xs shadow-lg transition-all cursor-pointer active:scale-98"
          >
            {isFa ? 'ارسال درخواست جهت بررسی و صدور مجوز' : 'Submit Partner Application'}
          </button>
        </form>
      </div>
    </div>
  );
};
