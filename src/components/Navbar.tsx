import React from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import {
  MapPin,
  Globe,
  Lock,
  LogOut,
  CalendarCheck,
  Building2,
  Shield,
  LifeBuoy,
  UserPlus,
  Sparkles,
} from 'lucide-react';

export const Navbar: React.FC = () => {
  const {
    language,
    userRole,
    selectedDistrict,
    toggleLanguage,
    openLocationSwitcher,
    openOnboarding,
    openSupport,
    openAdminLogin,
    logout,
    openBooking,
  } = useAuraLuxe();

  const isFa = language === 'FA';

  return (
    <header className="sticky top-0 z-40 bg-[#090A0D]/90 backdrop-blur-md border-b border-[#232736]/70 px-4 lg:px-8 py-3.5 transition-all">
      <div className="max-w-7xl mx-auto flex items-center justify-between gap-3">
        {/* Brand / Logo */}
        <div className="flex items-center gap-3">
          <div className="relative flex items-center justify-center w-10 h-10 rounded-xl bg-gradient-to-br from-[#E5C378] to-[#997935] shadow-[0_0_15px_rgba(229,195,120,0.25)] border border-[#FBF4E2]/40">
            <span className="font-serif font-black text-lg text-[#090A0D] tracking-tighter">A</span>
            <Sparkles className="w-3 h-3 text-[#090A0D] absolute top-1 right-1 opacity-80" />
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-serif text-lg lg:text-xl font-bold tracking-wider text-white">
                AURA LUXE
              </span>
              <span className="text-[10px] tracking-widest text-[#E5C378] uppercase px-1.5 py-0.5 rounded-full border border-[#E5C378]/30 bg-[#E5C378]/10 font-mono">
                B2B
              </span>
            </div>
            <p className="text-[10px] text-[#9CA3AF] hidden sm:block">
              {isFa
                ? 'پلتفرم رزرو سوئیت‌های بالینی و زیبایی ممتاز'
                : 'Luxury Clinical & Aesthetic Suite Matrix'}
            </p>
          </div>
        </div>

        {/* Location Selector Pill */}
        <button
          onClick={openLocationSwitcher}
          className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-[#11131A] border border-[#232736] hover:border-[#E5C378]/50 hover:bg-[#161822] transition-all cursor-pointer group"
          title={isFa ? 'تغییر منطقه' : 'Switch District'}
        >
          <div className="w-6 h-6 rounded-lg bg-[#E5C378]/10 flex items-center justify-center text-[#E5C378] group-hover:scale-110 transition-transform">
            <MapPin className="w-3.5 h-3.5" />
          </div>
          <div className="text-right">
            <div className="text-[10px] text-[#9CA3AF] leading-none">
              {selectedDistrict.cityId === 'tehran'
                ? isFa
                  ? 'تهران'
                  : 'Tehran'
                : isFa
                ? 'کرج'
                : 'Karaj'}
            </div>
            <div className="text-xs font-bold text-white group-hover:text-[#E5C378] transition-colors leading-tight">
              {isFa ? selectedDistrict.nameFa : selectedDistrict.nameEn}
            </div>
          </div>
        </button>

        {/* Actions & Role Controls */}
        <div className="flex items-center gap-2 lg:gap-3">
          {/* Language Toggle */}
          <button
            onClick={toggleLanguage}
            className="flex items-center gap-1.5 px-2.5 py-1.5 rounded-xl bg-[#11131A] border border-[#232736] hover:border-[#E5C378]/40 text-xs font-medium text-[#F9FAFB] hover:text-[#E5C378] transition-all cursor-pointer"
            title="Toggle Language"
          >
            <Globe className="w-3.5 h-3.5 text-[#E5C378]" />
            <span className="font-mono text-xs font-semibold">{language}</span>
          </button>

          {/* Support Tickets */}
          <button
            onClick={openSupport}
            className="hidden sm:flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#11131A] border border-[#232736] hover:border-[#E5C378]/40 text-xs text-[#9CA3AF] hover:text-white transition-all cursor-pointer"
            title={isFa ? 'پشتیبانی فنی و مالی' : 'Support Tickets'}
          >
            <LifeBuoy className="w-3.5 h-3.5 text-[#38BDF8]" />
            <span className="hidden md:inline">{isFa ? 'پشتیبانی' : 'Support'}</span>
          </button>

          {/* Partner Onboarding */}
          <button
            onClick={openOnboarding}
            className="hidden md:flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#11131A] border border-[#232736] hover:border-[#E5C378]/40 text-xs text-[#9CA3AF] hover:text-white transition-all cursor-pointer"
            title={isFa ? 'ثبت‌نام مراکز' : 'Partner Sign-Up'}
          >
            <UserPlus className="w-3.5 h-3.5 text-[#E5C378]" />
            <span>{isFa ? 'همکاری مراکز' : 'Partner'}</span>
          </button>

          {/* User Role Badge / Authentication Button */}
          {userRole === 'PUBLIC' ? (
            <button
              onClick={openAdminLogin}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#11131A] border border-[#232736] hover:border-[#E5C378]/60 text-xs text-[#E5C378] transition-all cursor-pointer font-medium"
            >
              <Lock className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">{isFa ? 'ورود مدیریت' : 'Executive'}</span>
            </button>
          ) : userRole === 'SUPER_ADMIN' ? (
            <div className="flex items-center gap-2">
              <span className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-amber-500/15 border border-amber-500/40 text-amber-300 text-xs font-bold">
                <Shield className="w-3.5 h-3.5" />
                <span>{isFa ? 'سوپرادمین' : 'SuperAdmin'}</span>
              </span>
              <button
                onClick={logout}
                className="p-1.5 rounded-xl bg-[#161822] border border-rose-500/40 text-rose-400 hover:bg-rose-500/20 transition-all cursor-pointer"
                title={isFa ? 'خروج از حساب' : 'Logout'}
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <span className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-[#E5C378]/15 border border-[#E5C378]/40 text-[#E5C378] text-xs font-bold">
                <Building2 className="w-3.5 h-3.5" />
                <span className="hidden sm:inline">{isFa ? 'مدیر کلینیک' : 'Clinic Director'}</span>
              </span>
              <button
                onClick={logout}
                className="p-1.5 rounded-xl bg-[#161822] border border-rose-500/40 text-rose-400 hover:bg-rose-500/20 transition-all cursor-pointer"
                title={isFa ? 'خروج از حساب' : 'Logout'}
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          )}

          {/* Clinical Booking Primary CTA */}
          {userRole === 'PUBLIC' && (
            <button
              onClick={() => openBooking()}
              className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-[#090A0D] text-xs font-bold shadow-[0_2px_12px_rgba(229,195,120,0.3)] transition-all cursor-pointer active:scale-95"
            >
              <CalendarCheck className="w-3.5 h-3.5" />
              <span>{isFa ? 'درخواست نوبت' : 'Book Suite'}</span>
            </button>
          )}
        </div>
      </div>
    </header>
  );
};
