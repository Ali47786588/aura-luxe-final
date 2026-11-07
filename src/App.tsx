import React from 'react';
import { AuraLuxeProvider, useAuraLuxe } from './context/AuraLuxeContext';
import { Navbar } from './components/Navbar';
import { HeroSection } from './components/HeroSection';
import { InstagramStoryGallery } from './components/InstagramStoryGallery';
import { VenueCatalog } from './components/VenueCatalog';
import { RealtimeSlotPanel } from './components/RealtimeSlotPanel';
import { BeforeAfterSlider } from './components/BeforeAfterSlider';
import { PartnerCalloutCard } from './components/PartnerCalloutCard';
import { LuxuryFooter } from './components/LuxuryFooter';
import { ClinicAdminPortal } from './components/ClinicAdminPortal';
import { SuperAdminPanel } from './components/SuperAdminPanel';
import { ToastNotificationContainer } from './components/ToastNotificationContainer';

// Modals
import { BookingRequestModal } from './components/modals/BookingRequestModal';
import { VenueDetailModal } from './components/modals/VenueDetailModal';
import { LocationSwitcherModal } from './components/modals/LocationSwitcherModal';
import { AdminLoginModal } from './components/modals/AdminLoginModal';
import { OnboardingModal } from './components/modals/OnboardingModal';
import { SupportTicketingModal } from './components/modals/SupportTicketingModal';
import { StoryViewerDialog } from './components/modals/StoryViewerDialog';
import { DigitalSignaturePadDialog } from './components/modals/DigitalSignaturePadDialog';
import { InvoiceDialog } from './components/modals/InvoiceDialog';

const AppContent: React.FC = () => {
  const { userRole, activeModal, language } = useAuraLuxe();

  const handleScrollToSlots = () => {
    const el = document.getElementById('realtime-slots');
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div
      className="min-h-screen bg-[#090A0D] text-[#F9FAFB] flex flex-col font-sans transition-colors duration-200"
      dir={language === 'FA' ? 'rtl' : 'ltr'}
    >
      {/* Sticky Navbar */}
      <Navbar />

      {/* Main Content View (Role-Driven) */}
      <main className="flex-1">
        {userRole === 'SUPER_ADMIN' ? (
          <SuperAdminPanel />
        ) : userRole === 'CLINIC_ADMIN' ? (
          <ClinicAdminPortal />
        ) : (
          <div className="space-y-6">
            <HeroSection onScrollToSlots={handleScrollToSlots} />
            <InstagramStoryGallery />
            <VenueCatalog />
            <RealtimeSlotPanel />
            <BeforeAfterSlider />
            <PartnerCalloutCard />
            <LuxuryFooter />
          </div>
        )}
      </main>

      {/* Mutually Exclusive Modal Layer */}
      {activeModal === 'BOOKING_REQUEST' && <BookingRequestModal />}
      {activeModal === 'VENUE_DETAILS' && <VenueDetailModal />}
      {activeModal === 'LOCATION_SWITCHER' && <LocationSwitcherModal />}
      {activeModal === 'ADMIN_LOGIN' && <AdminLoginModal />}
      {activeModal === 'ONBOARDING' && <OnboardingModal />}
      {activeModal === 'SUPPORT_TICKETING' && <SupportTicketingModal />}
      {activeModal === 'STORY_VIEWER' && <StoryViewerDialog />}
      {activeModal === 'DIGITAL_SIGNATURE' && <DigitalSignaturePadDialog />}
      {activeModal === 'INVOICE_DETAILS' && <InvoiceDialog />}

      {/* Toast Notification Layer */}
      <ToastNotificationContainer />
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <AuraLuxeProvider>
      <AppContent />
    </AuraLuxeProvider>
  );
};

export default App;
