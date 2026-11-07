import React, { useState } from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { SlotStatus, BookingRequestStatus, Venue } from '../types';
import { formatPrice } from '../utils/localization';
import {
  Building2,
  LogOut,
  CalendarCheck,
  CheckCircle2,
  XCircle,
  Activity,
  Layers,
  Sparkles,
  Phone,
  Clock,
  User,
  Shield,
  FileText,
} from 'lucide-react';

export const ClinicAdminPortal: React.FC = () => {
  const {
    language,
    venues,
    managedVenue,
    setManagedVenue,
    slots,
    bookingRequests,
    toggleSlotStatus,
    updateBookingRequestStatus,
    logout,
  } = useAuraLuxe();

  const isFa = language === 'FA';
  const [requestTab, setRequestTab] = useState<'PENDING' | 'CONFIRMED'>('PENDING');

  const venueSlots = slots.filter((s) => s.venueId === managedVenue.id);
  const pendingRequests = bookingRequests.filter(
    (r) => r.venueId === managedVenue.id && r.status === 'PENDING_CONFIRMATION'
  );
  const confirmedRequests = bookingRequests.filter(
    (r) => r.venueId === managedVenue.id && r.status === 'CONFIRMED'
  );

  const availableCount = venueSlots.filter((s) => s.status === 'AVAILABLE').length;
  const inProcedureCount = venueSlots.filter((s) => s.status === 'IN_PROCEDURE').length;
  const reservedCount = venueSlots.filter((s) => s.status === 'RESERVED').length;

  const occupancyRate =
    venueSlots.length > 0
      ? Math.round(((reservedCount + inProcedureCount) * 100) / venueSlots.length)
      : 65;

  const todayRevenue = managedVenue.baseHourlyToman * 5;

  return (
    <div className="mx-4 lg:mx-8 py-6 space-y-6">
      {/* Executive Portal Header Banner */}
      <div className="p-6 rounded-3xl bg-[#11131A] border border-[#E5C378]/30 shadow-xl">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
          <div className="flex items-center gap-3.5">
            <div className="w-12 h-12 rounded-2xl bg-[#E5C378] text-[#090A0D] flex items-center justify-center font-bold shadow-lg">
              <Building2 className="w-6 h-6" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-[#E5C378] tracking-wider uppercase font-mono">
                  {isFa ? 'پرتال اختصاصی مدیریت مراکز' : 'EXECUTIVE CLINIC PORTAL'}
                </span>
                <span className="px-2 py-0.5 rounded-full bg-[#E5C378]/15 border border-[#E5C378]/30 text-[#E5C378] text-[10px] font-mono">
                  DIRECTOR MODE
                </span>
              </div>
              <h1 className="text-xl sm:text-2xl font-extrabold text-white">
                {isFa ? managedVenue.nameFa : managedVenue.nameEn}
              </h1>
              <p className="text-xs text-[#9CA3AF] mt-0.5">
                {isFa
                  ? `مدیر درمانی و فنی: ${managedVenue.medicalDirectorFa}`
                  : `Medical Director: ${managedVenue.medicalDirectorEn}`}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {/* Switch Managed Venue Dropdown */}
            <select
              value={managedVenue.id}
              onChange={(e) => {
                const found = venues.find((v) => v.id === e.target.value);
                if (found) setManagedVenue(found);
              }}
              className="px-3 py-2 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white focus:outline-none focus:border-[#E5C378] cursor-pointer"
            >
              {venues.map((v) => (
                <option key={v.id} value={v.id}>
                  {isFa ? v.nameFa : v.nameEn}
                </option>
              ))}
            </select>

            <button
              onClick={logout}
              className="flex items-center gap-1.5 px-3.5 py-2 rounded-xl bg-rose-500/15 border border-rose-500/30 text-rose-400 hover:bg-rose-500/25 text-xs font-semibold transition-all cursor-pointer"
            >
              <LogOut className="w-4 h-4" />
              <span>{isFa ? 'خروج از پنل' : 'Exit Portal'}</span>
            </button>
          </div>
        </div>

        {/* Executive KPIs */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-4 border-t border-[#232736]">
          <div className="p-3.5 rounded-2xl bg-[#161822] border border-[#232736]">
            <div className="text-[11px] text-[#9CA3AF] mb-1">
              {isFa ? 'ضریب اشغال یونیت‌ها' : 'Occupancy Rate'}
            </div>
            <div className="text-xl font-extrabold text-white font-mono">
              {occupancyRate}%
            </div>
          </div>

          <div className="p-3.5 rounded-2xl bg-[#161822] border border-[#232736]">
            <div className="text-[11px] text-[#9CA3AF] mb-1">
              {isFa ? 'یونیت‌های آماده به کار' : 'Instant Ready'}
            </div>
            <div className="text-xl font-extrabold text-emerald-400 font-mono">
              {availableCount} {isFa ? 'از' : 'of'} {venueSlots.length}
            </div>
          </div>

          <div className="p-3.5 rounded-2xl bg-[#161822] border border-[#232736]">
            <div className="text-[11px] text-[#9CA3AF] mb-1">
              {isFa ? 'نوبت‌های در صف تایید' : 'Pending Bookings'}
            </div>
            <div className="text-xl font-extrabold text-amber-400 font-mono">
              {pendingRequests.length}
            </div>
          </div>

          <div className="p-3.5 rounded-2xl bg-[#161822] border border-[#232736]">
            <div className="text-[11px] text-[#9CA3AF] mb-1">
              {isFa ? 'درآمد تخمینی شیفت امروز' : 'Today Projected Gross'}
            </div>
            <div className="text-lg font-bold text-[#E5C378] font-mono">
              {formatPrice(todayRevenue, language)}
            </div>
          </div>
        </div>
      </div>

      {/* Grid: Slots Real-time Controls & Bookings Management */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Section 1: Real-time Unit Allocation */}
        <div className="p-6 rounded-3xl bg-[#11131A] border border-[#232736]">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Activity className="w-5 h-5 text-emerald-400" />
              <h2 className="text-base font-bold text-white">
                {isFa ? 'مدیریت وضعیت یونیت‌ها و تخت‌ها' : 'Live Suite & Chair Allocation'}
              </h2>
            </div>
            <span className="text-xs text-[#9CA3AF]">
              {isFa ? 'تغییر آنی وضعیت در سامانه' : 'Instant state toggle'}
            </span>
          </div>

          <div className="space-y-4">
            {venueSlots.map((slot) => (
              <div
                key={slot.id}
                className="p-4 rounded-2xl bg-[#161822] border border-[#232736] space-y-3"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <h3 className="text-sm font-bold text-white">
                      {isFa ? slot.roomNameFa : slot.roomNameEn}
                    </h3>
                    <span className="text-[11px] text-[#9CA3AF] font-mono uppercase">
                      {slot.type} • {formatPrice(slot.pricePerHourToman, language)}/h
                    </span>
                  </div>
                  <span
                    className={`px-2 py-0.5 rounded-full text-[10px] font-semibold font-mono ${
                      slot.status === 'AVAILABLE'
                        ? 'bg-emerald-500/20 text-emerald-300'
                        : slot.status === 'IN_PROCEDURE'
                        ? 'bg-rose-500/20 text-rose-300'
                        : slot.status === 'SANITIZING'
                        ? 'bg-sky-500/20 text-sky-300'
                        : 'bg-amber-500/20 text-amber-300'
                    }`}
                  >
                    {slot.status}
                  </span>
                </div>

                {/* 4-way State Switcher Buttons */}
                <div className="grid grid-cols-4 gap-1.5 p-1 rounded-xl bg-[#090A0D] border border-[#232736]">
                  {(['AVAILABLE', 'IN_PROCEDURE', 'SANITIZING', 'RESERVED'] as SlotStatus[]).map(
                    (st) => {
                      const isActive = slot.status === st;
                      const labels = {
                        AVAILABLE: isFa ? 'آزاد' : 'Free',
                        IN_PROCEDURE: isFa ? 'در جراحی' : 'Busy',
                        SANITIZING: isFa ? 'استریل' : 'Clean',
                        RESERVED: isFa ? 'رزرو' : 'Hold',
                      };
                      return (
                        <button
                          key={st}
                          onClick={() => toggleSlotStatus(slot.id, st)}
                          className={`py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
                            isActive
                              ? st === 'AVAILABLE'
                                ? 'bg-emerald-500 text-black shadow'
                                : st === 'IN_PROCEDURE'
                                ? 'bg-rose-500 text-white shadow'
                                : st === 'SANITIZING'
                                ? 'bg-sky-500 text-black shadow'
                                : 'bg-amber-500 text-black shadow'
                              : 'text-[#9CA3AF] hover:text-white hover:bg-white/5'
                          }`}
                        >
                          {labels[st]}
                        </button>
                      );
                    }
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Section 2: Booking Requests Approvals */}
        <div className="p-6 rounded-3xl bg-[#11131A] border border-[#232736]">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <CalendarCheck className="w-5 h-5 text-[#E5C378]" />
              <h2 className="text-base font-bold text-white">
                {isFa ? 'کارتابل درخواست‌های رزرو متقاضیان' : 'Booking Inquiries & Approvals'}
              </h2>
            </div>

            {/* Sub-tabs: Pending / Confirmed */}
            <div className="flex items-center gap-1 p-1 rounded-xl bg-[#090A0D] border border-[#232736]">
              <button
                onClick={() => setRequestTab('PENDING')}
                className={`px-3 py-1 rounded-lg text-xs font-semibold cursor-pointer ${
                  requestTab === 'PENDING'
                    ? 'bg-[#E5C378] text-black'
                    : 'text-[#9CA3AF] hover:text-white'
                }`}
              >
                {isFa ? `در انتظار (${pendingRequests.length})` : `Pending (${pendingRequests.length})`}
              </button>
              <button
                onClick={() => setRequestTab('CONFIRMED')}
                className={`px-3 py-1 rounded-lg text-xs font-semibold cursor-pointer ${
                  requestTab === 'CONFIRMED'
                    ? 'bg-[#E5C378] text-black'
                    : 'text-[#9CA3AF] hover:text-white'
                }`}
              >
                {isFa ? `تایید شده (${confirmedRequests.length})` : `Confirmed (${confirmedRequests.length})`}
              </button>
            </div>
          </div>

          <div className="space-y-4 max-h-[500px] overflow-y-auto pr-1">
            {(requestTab === 'PENDING' ? pendingRequests : confirmedRequests).length > 0 ? (
              (requestTab === 'PENDING' ? pendingRequests : confirmedRequests).map((req) => (
                <div
                  key={req.id}
                  className="p-4 rounded-2xl bg-[#161822] border border-[#232736] space-y-3"
                >
                  <div className="flex items-center justify-between">
                    <div>
                      <span className="text-xs font-bold text-white">{req.fullName}</span>
                      <span className="text-[11px] text-[#E5C378] font-mono mr-2">
                        {req.trackingCode}
                      </span>
                    </div>
                    <span className="text-[10px] text-[#9CA3AF]">{req.createdAt}</span>
                  </div>

                  <div className="grid grid-cols-2 gap-2 text-xs text-[#9CA3AF]">
                    <div className="flex items-center gap-1.5">
                      <Phone className="w-3.5 h-3.5 text-[#E5C378]" />
                      <span className="font-mono">{req.phoneNumber}</span>
                    </div>
                    <div className="flex items-center gap-1.5">
                      <Clock className="w-3.5 h-3.5 text-[#38BDF8]" />
                      <span>{req.suggestedTime}</span>
                    </div>
                  </div>

                  <div className="text-xs text-white bg-[#090A0D] p-2.5 rounded-xl border border-[#232736]">
                    <span className="text-[10px] text-[#9CA3AF] block">
                      {isFa ? 'خدمت بالینی مورد نظر:' : 'Requested Service:'}
                    </span>
                    <span className="font-semibold">{req.desiredService}</span>
                    {req.notes && (
                      <p className="text-[11px] text-[#9CA3AF] mt-1 italic">"{req.notes}"</p>
                    )}
                  </div>

                  {requestTab === 'PENDING' && (
                    <div className="flex items-center gap-2 pt-2 border-t border-[#232736]">
                      <button
                        onClick={() => updateBookingRequestStatus(req.id, 'CONFIRMED')}
                        className="flex-1 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-black font-bold text-xs flex items-center justify-center gap-1.5 transition-colors cursor-pointer"
                      >
                        <CheckCircle2 className="w-4 h-4" />
                        <span>{isFa ? 'تایید و صدور پیش‌فاکتور' : 'Confirm Booking'}</span>
                      </button>

                      <button
                        onClick={() => updateBookingRequestStatus(req.id, 'REJECTED')}
                        className="py-2 px-3 rounded-xl bg-rose-500/15 hover:bg-rose-500/25 border border-rose-500/30 text-rose-400 font-semibold text-xs flex items-center justify-center gap-1.5 transition-colors cursor-pointer"
                      >
                        <XCircle className="w-4 h-4" />
                        <span>{isFa ? 'عدم امکان' : 'Decline'}</span>
                      </button>
                    </div>
                  )}
                </div>
              ))
            ) : (
              <div className="py-12 text-center text-xs text-[#9CA3AF]">
                {isFa ? 'موردی در این بخش وجود ندارد.' : 'No records in this category.'}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
