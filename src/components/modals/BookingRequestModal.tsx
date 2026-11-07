import React, { useState } from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { formatPrice } from '../../utils/localization';
import {
  X,
  CalendarCheck,
  Building2,
  Clock,
  User,
  Phone,
  FileText,
  CheckCircle2,
  Sparkles,
} from 'lucide-react';

export const BookingRequestModal: React.FC = () => {
  const {
    language,
    venues,
    preselectedBookingVenue,
    preselectedBookingSlot,
    submittedBookingRequest,
    closeModal,
    submitBookingRequest,
  } = useAuraLuxe();

  const isFa = language === 'FA';
  const targetVenue = preselectedBookingVenue || venues[0];

  const [fullName, setFullName] = useState<string>('');
  const [phoneNumber, setPhoneNumber] = useState<string>('');
  const [desiredService, setDesiredService] = useState<string>('');
  const [suggestedDate, setSuggestedDate] = useState<string>('2026-11-08');
  const [suggestedTime, setSuggestedTime] = useState<string>('10:00 - 12:00 (صبح)');
  const [notes, setNotes] = useState<string>('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim() || !phoneNumber.trim() || !desiredService.trim()) return;
    submitBookingRequest(fullName, phoneNumber, desiredService, suggestedDate, suggestedTime, notes);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 overflow-y-auto animate-fadeIn">
      <div className="relative w-full max-w-lg bg-[#11131A] border border-[#E5C378]/40 rounded-3xl p-6 sm:p-8 shadow-2xl my-8">
        {/* Close Button */}
        <button
          onClick={closeModal}
          className="absolute top-5 left-5 w-8 h-8 rounded-full bg-[#161822] border border-[#232736] flex items-center justify-center text-[#9CA3AF] hover:text-white hover:bg-[#232736] transition-colors cursor-pointer"
        >
          <X className="w-4 h-4" />
        </button>

        {submittedBookingRequest ? (
          /* Confirmation Success State */
          <div className="text-center py-6 space-y-4">
            <div className="w-16 h-16 rounded-full bg-emerald-500/20 border-2 border-emerald-500/50 flex items-center justify-center mx-auto text-emerald-400">
              <CheckCircle2 className="w-8 h-8" />
            </div>

            <h2 className="text-xl font-bold text-white">
              {isFa ? 'درخواست رزرو با موفقیت ثبت شد' : 'Reservation Successfully Queued'}
            </h2>

            <p className="text-xs text-[#9CA3AF] max-w-sm mx-auto">
              {isFa
                ? 'درخواست شما در کارتابل پذیرش کلینیک ثبت گردید و پس از بررسی، از طریق پیامک اطلاع‌رسانی خواهد شد.'
                : 'Your inquiry has been placed into the facility queue. You will receive an SMS confirmation upon clinical approval.'}
            </p>

            <div className="p-4 rounded-2xl bg-[#161822] border border-[#232736] text-right space-y-2">
              <div className="flex items-center justify-between text-xs">
                <span className="text-[#9CA3AF]">{isFa ? 'کد رهگیری سامانه:' : 'Tracking Code:'}</span>
                <span className="font-mono font-bold text-[#E5C378] text-sm">
                  {submittedBookingRequest.trackingCode}
                </span>
              </div>
              <div className="flex items-center justify-between text-xs">
                <span className="text-[#9CA3AF]">{isFa ? 'مرکز درمانی:' : 'Center:'}</span>
                <span className="font-semibold text-white">
                  {isFa ? submittedBookingRequest.venueNameFa : submittedBookingRequest.venueNameEn}
                </span>
              </div>
              <div className="flex items-center justify-between text-xs">
                <span className="text-[#9CA3AF]">{isFa ? 'خدمت بالینی:' : 'Service:'}</span>
                <span className="text-white">{submittedBookingRequest.desiredService}</span>
              </div>
              <div className="flex items-center justify-between text-xs">
                <span className="text-[#9CA3AF]">{isFa ? 'زمان پیشنهادی:' : 'Suggested Time:'}</span>
                <span className="font-mono text-white">
                  {submittedBookingRequest.suggestedDate} • {submittedBookingRequest.suggestedTime}
                </span>
              </div>
            </div>

            <button
              onClick={closeModal}
              className="w-full py-3 rounded-xl bg-[#E5C378] hover:bg-[#FBF4E2] text-[#090A0D] font-bold text-xs transition-colors cursor-pointer"
            >
              {isFa ? 'متوجه شدم و بازگشت' : 'Done'}
            </button>
          </div>
        ) : (
          /* Booking Form */
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <div className="flex items-center gap-1.5 text-xs text-[#E5C378] font-mono uppercase mb-1">
                <CalendarCheck className="w-3.5 h-3.5" />
                <span>{isFa ? 'فرم رزرو نوبت و سوئیت' : 'CLINICAL SUITE BOOKING'}</span>
              </div>
              <h2 className="text-lg font-bold text-white">
                {isFa ? 'ثبت درخواست رزرو بالینی' : 'Book Clinical Suite'}
              </h2>
            </div>

            {/* Target Venue & Slot Banner */}
            <div className="p-3 rounded-2xl bg-[#161822] border border-[#232736] flex items-center gap-3">
              <Building2 className="w-5 h-5 text-[#E5C378]" />
              <div className="flex-1">
                <div className="text-xs font-bold text-white">
                  {isFa ? targetVenue.nameFa : targetVenue.nameEn}
                </div>
                <div className="text-[11px] text-[#9CA3AF]">
                  {preselectedBookingSlot
                    ? isFa
                      ? preselectedBookingSlot.roomNameFa
                      : preselectedBookingSlot.roomNameEn
                    : isFa
                    ? `سوئیت‌های استاندارد (${formatPrice(targetVenue.baseHourlyToman, language)}/h)`
                    : `Standard Suites (${formatPrice(targetVenue.baseHourlyToman, language)}/h)`}
                </div>
              </div>
            </div>

            {/* Full Name & Phone */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-[11px] text-[#9CA3AF] block mb-1">
                  {isFa ? 'نام و نام خانوادگی پزشک / مراجع:' : 'Full Name:'}
                </label>
                <input
                  type="text"
                  required
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder={isFa ? 'دکتر آرین فرزین' : 'Dr. Arian Farzin'}
                  className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
                />
              </div>

              <div>
                <label className="text-[11px] text-[#9CA3AF] block mb-1">
                  {isFa ? 'شماره تماس هماهنگی:' : 'Phone Number:'}
                </label>
                <input
                  type="tel"
                  required
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  placeholder="09121234567"
                  className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] font-mono focus:outline-none focus:border-[#E5C378]"
                />
              </div>
            </div>

            {/* Desired Service */}
            <div>
              <label className="text-[11px] text-[#9CA3AF] block mb-1">
                {isFa ? 'نوع اقدام درمانی یا زیبایی:' : 'Desired Clinical Service:'}
              </label>
              <input
                type="text"
                required
                value={desiredService}
                onChange={(e) => setDesiredService(e.target.value)}
                placeholder={
                  isFa
                    ? 'مثال: تزریق ژل و بوتاکس، لمینت سرامیکی، لیزر فول بادی'
                    : 'e.g. Veneer Bonding, Candela Laser, Injectables'
                }
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
              />
            </div>

            {/* Date & Time Slot */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="text-[11px] text-[#9CA3AF] block mb-1">
                  {isFa ? 'تاریخ پیشنهادی:' : 'Suggested Date:'}
                </label>
                <input
                  type="date"
                  value={suggestedDate}
                  onChange={(e) => setSuggestedDate(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white font-mono focus:outline-none focus:border-[#E5C378]"
                />
              </div>

              <div>
                <label className="text-[11px] text-[#9CA3AF] block mb-1">
                  {isFa ? 'شیفت زمانی:' : 'Time Window:'}
                </label>
                <select
                  value={suggestedTime}
                  onChange={(e) => setSuggestedTime(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white focus:outline-none focus:border-[#E5C378] cursor-pointer"
                >
                  <option value="10:00 - 12:00 (صبح)">10:00 - 12:00 (صبح)</option>
                  <option value="12:00 - 15:00 (ظهر)">12:00 - 15:00 (ظهر)</option>
                  <option value="16:00 - 18:00 (عصر)">16:00 - 18:00 (عصر)</option>
                  <option value="18:00 - 21:00 (شب)">18:00 - 21:00 (شب)</option>
                </select>
              </div>
            </div>

            {/* Notes */}
            <div>
              <label className="text-[11px] text-[#9CA3AF] block mb-1">
                {isFa ? 'توضیحات و نیازمندی‌های اتاق عمل:' : 'Clinical Notes & Equipment Needs:'}
              </label>
              <textarea
                rows={2}
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder={isFa ? 'نیاز به نرس همراه یا تیپ استریل...' : 'Sterile packs, assistant nurse, etc.'}
                className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
              />
            </div>

            {/* Submit CTA */}
            <button
              type="submit"
              className="w-full py-3 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-[#090A0D] font-bold text-xs shadow-lg transition-all cursor-pointer active:scale-98"
            >
              {isFa ? 'ثبت و ارسال به کارتابل کلینیک' : 'Submit Booking Request'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};
