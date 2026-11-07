import React from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { RealtimeSlot, SlotStatus } from '../types';
import { formatPrice } from '../utils/localization';
import {
  Activity,
  CheckCircle2,
  Clock,
  Shield,
  UserCheck,
  Sparkles,
  Zap,
  Building2,
  CalendarCheck,
} from 'lucide-react';

export const RealtimeSlotPanel: React.FC = () => {
  const { language, slots, venues, openBooking } = useAuraLuxe();
  const isFa = language === 'FA';

  const getStatusBadge = (status: SlotStatus) => {
    switch (status) {
      case 'AVAILABLE':
        return {
          labelFa: 'آزاد جهت رزرو آنی',
          labelEn: 'Instant Available',
          className: 'bg-emerald-500/15 border-emerald-500/40 text-emerald-400',
          dotClass: 'bg-emerald-400',
        };
      case 'IN_PROCEDURE':
        return {
          labelFa: 'در حال انجام جراحی / درمان',
          labelEn: 'In Procedure',
          className: 'bg-rose-500/15 border-rose-500/40 text-rose-400',
          dotClass: 'bg-rose-400',
        };
      case 'RESERVED':
        return {
          labelFa: 'رزرو شده (تایید نهایی)',
          labelEn: 'Reserved',
          className: 'bg-amber-500/15 border-amber-500/40 text-amber-400',
          dotClass: 'bg-amber-400',
        };
      case 'SANITIZING':
        return {
          labelFa: 'فرآیند استریلیزاسیون و اتوکلاو',
          labelEn: 'Sanitizing / Autoclave',
          className: 'bg-sky-500/15 border-sky-500/40 text-sky-400',
          dotClass: 'bg-sky-400',
        };
    }
  };

  return (
    <section id="realtime-slots" className="mx-4 lg:mx-8 py-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6 p-5 rounded-2xl bg-[#11131A] border border-[#232736]">
        <div>
          <div className="flex items-center gap-2 text-emerald-400 text-xs font-bold font-mono tracking-wider uppercase mb-1">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75" />
              <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500" />
            </span>
            <span>{isFa ? 'تخصیص بلادرنگ یونیت‌ها' : 'LIVE SUITE TELEMETRY'}</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-bold text-white">
            {isFa ? 'ماتریس زنده سوئیت‌ها و یونیت‌های بالینی' : 'Real-time Operatory & Laser Matrix'}
          </h2>
        </div>

        <div className="text-xs text-[#9CA3AF] flex items-center gap-2">
          <Shield className="w-4 h-4 text-[#E5C378]" />
          <span>
            {isFa
              ? 'اتوکلاو کلاس B و بازرسی بهداشتی پیش از تحویل'
              : 'ISO Cleanroom & Autoclave Protocol Active'}
          </span>
        </div>
      </div>

      {/* Slots Matrix */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {slots.map((slot) => {
          const badge = getStatusBadge(slot.status);
          const venue = venues.find((v) => v.id === slot.venueId);

          return (
            <div
              key={slot.id}
              className="p-5 rounded-3xl bg-[#11131A] border border-[#232736] hover:border-[#E5C378]/40 transition-all flex flex-col justify-between"
            >
              <div>
                {/* Status & Type Bar */}
                <div className="flex items-center justify-between gap-2 mb-3">
                  <span
                    className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[11px] font-semibold border ${badge.className}`}
                  >
                    <span className={`w-1.5 h-1.5 rounded-full ${badge.dotClass}`} />
                    <span>{isFa ? badge.labelFa : badge.labelEn}</span>
                  </span>

                  <span className="text-[10px] font-mono uppercase tracking-wider text-[#9CA3AF] px-2 py-0.5 rounded-md bg-[#161822] border border-[#232736]">
                    {slot.type.replace('_', ' ')}
                  </span>
                </div>

                {/* Slot Title */}
                <h3 className="text-base font-bold text-white mb-1">
                  {isFa ? slot.roomNameFa : slot.roomNameEn}
                </h3>

                {/* Venue Name */}
                <div className="flex items-center gap-1.5 text-xs text-[#E5C378] mb-4">
                  <Building2 className="w-3.5 h-3.5" />
                  <span>{isFa ? slot.venueNameFa : slot.venueNameEn}</span>
                </div>

                {/* Active Practitioner or Sanitizing Status */}
                {slot.status === 'IN_PROCEDURE' && slot.currentPractitioner && (
                  <div className="p-3 rounded-xl bg-rose-500/10 border border-rose-500/20 mb-3 text-xs">
                    <div className="font-semibold text-rose-300 mb-0.5">
                      {slot.currentPractitioner}
                    </div>
                    <div className="text-[11px] text-[#9CA3AF]">{slot.patientProcedure}</div>
                  </div>
                )}

                {slot.status === 'SANITIZING' && (
                  <div className="p-3 rounded-xl bg-sky-500/10 border border-sky-500/20 mb-3 text-xs text-sky-300 flex items-center gap-2">
                    <Clock className="w-4 h-4 text-sky-400" />
                    <span>
                      {isFa
                        ? `استریل UV-C از ${slot.lastSanitizedMinutesAgo} دقیقه پیش فعال است`
                        : `UV-C Sterilization in progress (${slot.lastSanitizedMinutesAgo}m)`}
                    </span>
                  </div>
                )}

                {/* Equipment Badges */}
                <div className="flex flex-wrap gap-1.5 mb-4">
                  {slot.equipment.map((eq, i) => (
                    <span
                      key={i}
                      className="px-2 py-0.5 rounded-md bg-[#161822] border border-[#232736] text-[10px] text-[#9CA3AF]"
                    >
                      {eq}
                    </span>
                  ))}
                </div>

                {/* Clinical Features Checks */}
                <div className="grid grid-cols-2 gap-2 py-3 border-t border-[#232736] text-[11px] text-[#9CA3AF] mb-4">
                  <div className="flex items-center gap-1.5">
                    <UserCheck
                      className={`w-3.5 h-3.5 ${
                        slot.assistantIncluded ? 'text-emerald-400' : 'text-gray-500'
                      }`}
                    />
                    <span>{isFa ? 'نرس/دستیار همراه' : 'Assistant Support'}</span>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <CheckCircle2
                      className={`w-3.5 h-3.5 ${
                        slot.autoclaveReady ? 'text-emerald-400' : 'text-gray-500'
                      }`}
                    />
                    <span>{isFa ? 'پک استریل آماده' : 'Autoclave Pack'}</span>
                  </div>
                </div>
              </div>

              {/* Price & Action */}
              <div className="pt-3 border-t border-[#232736] flex items-center justify-between">
                <div>
                  <span className="text-[10px] text-[#9CA3AF] block leading-none mb-1">
                    {isFa ? 'تعرفه هر ساعت:' : 'Hourly Rate:'}
                  </span>
                  <span className="text-sm font-bold text-[#E5C378] font-mono">
                    {formatPrice(slot.pricePerHourToman, language)}
                  </span>
                </div>

                <button
                  onClick={() => openBooking(venue, slot)}
                  className={`px-4 py-2 rounded-xl text-xs font-bold transition-all cursor-pointer flex items-center gap-1.5 ${
                    slot.status === 'AVAILABLE'
                      ? 'bg-gradient-to-r from-[#E5C378] to-[#C5A059] text-[#090A0D] hover:from-[#FBF4E2] hover:to-[#E5C378] shadow-md active:scale-95'
                      : 'bg-[#161822] text-[#9CA3AF] hover:text-white border border-[#232736]'
                  }`}
                >
                  <CalendarCheck className="w-3.5 h-3.5" />
                  <span>
                    {slot.status === 'AVAILABLE'
                      ? isFa
                        ? 'رزرو آنی یونیت'
                        : 'Book Slot'
                      : isFa
                      ? 'پیش‌رزرو نوبت'
                      : 'Pre-book'}
                  </span>
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </section>
  );
};
