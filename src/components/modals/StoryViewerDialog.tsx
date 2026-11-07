import React, { useEffect, useState, useRef } from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { X, ChevronLeft, ChevronRight, Send, Sparkles, Building2, User, Calendar } from 'lucide-react';

export const StoryViewerDialog: React.FC = () => {
  const {
    language,
    activeStoryHighlight,
    activeStorySlideIndex,
    nextStorySlide,
    prevStorySlide,
    closeModal,
    openBooking,
    showToast,
  } = useAuraLuxe();

  const [progress, setProgress] = useState<number>(0);
  const [inquiryText, setInquiryText] = useState<string>('');
  const [isPaused, setIsPaused] = useState<boolean>(false);
  const isFa = language === 'FA';

  const currentSlide = activeStoryHighlight?.slides[activeStorySlideIndex];
  const durationMs = (currentSlide?.durationSeconds || 6) * 1000;
  const progressIntervalRef = useRef<number | null>(null);

  useEffect(() => {
    setProgress(0);
  }, [activeStorySlideIndex, activeStoryHighlight]);

  useEffect(() => {
    if (!currentSlide || isPaused) return;

    const stepMs = 50;
    progressIntervalRef.current = window.setInterval(() => {
      setProgress((prev) => {
        const next = prev + (stepMs / durationMs) * 100;
        if (next >= 100) {
          nextStorySlide();
          return 0;
        }
        return next;
      });
    }, stepMs);

    return () => {
      if (progressIntervalRef.current) clearInterval(progressIntervalRef.current);
    };
  }, [currentSlide, isPaused, durationMs, nextStorySlide]);

  if (!activeStoryHighlight || !currentSlide) return null;

  const handleSendInquiry = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inquiryText.trim()) return;
    showToast(
      'SUCCESS',
      isFa ? 'پیام ارسال شد' : 'Inquiry Delivered',
      isFa
        ? `پیام شما به ${currentSlide.doctorNameFa} تحویل داده شد.`
        : `Sent to ${currentSlide.doctorNameEn}`
    );
    setInquiryText('');
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/90 backdrop-blur-md p-3 sm:p-6 animate-fadeIn">
      <div
        className="relative w-full max-w-md h-[85vh] max-h-[780px] bg-[#090A0D] rounded-3xl overflow-hidden border border-[#E5C378]/30 shadow-2xl flex flex-col justify-between"
        onMouseEnter={() => setIsPaused(true)}
        onMouseLeave={() => setIsPaused(false)}
      >
        {/* Background Slide Image */}
        <div className="absolute inset-0 z-0">
          <img
            src={currentSlide.image}
            alt={isFa ? currentSlide.titleFa : currentSlide.titleEn}
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-[#090A0D] via-transparent to-[#090A0D]/70" />
        </div>

        {/* Top Controls & Multi-segment Progress Bar */}
        <div className="relative z-10 p-4">
          {/* Progress Bars */}
          <div className="flex items-center gap-1.5 mb-3">
            {activeStoryHighlight.slides.map((s, idx) => {
              const fillPercent =
                idx < activeStorySlideIndex
                  ? 100
                  : idx === activeStorySlideIndex
                  ? progress
                  : 0;
              return (
                <div
                  key={s.id}
                  className="h-1 flex-1 bg-white/25 rounded-full overflow-hidden"
                >
                  <div
                    className="h-full bg-[#E5C378] transition-all ease-linear"
                    style={{ width: `${fillPercent}%` }}
                  />
                </div>
              );
            })}
          </div>

          {/* Header Info */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-10 h-10 rounded-full border-2 border-[#E5C378] overflow-hidden bg-[#11131A]">
                <img
                  src={activeStoryHighlight.coverImage}
                  alt=""
                  className="w-full h-full object-cover"
                />
              </div>
              <div>
                <h4 className="text-sm font-bold text-white leading-tight">
                  {isFa ? currentSlide.doctorNameFa : currentSlide.doctorNameEn}
                </h4>
                <div className="flex items-center gap-1 text-[11px] text-[#E5C378]">
                  <Building2 className="w-3 h-3" />
                  <span>{isFa ? currentSlide.clinicNameFa : currentSlide.clinicNameEn}</span>
                </div>
              </div>
            </div>

            <button
              onClick={closeModal}
              className="w-8 h-8 rounded-full bg-black/60 border border-white/20 flex items-center justify-center text-white hover:bg-black/90 cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Navigation Touch Areas (Left / Right) */}
        <div className="absolute inset-0 z-10 flex">
          <div
            className="w-1/3 h-full cursor-pointer"
            onClick={prevStorySlide}
            title={isFa ? 'قبلی' : 'Previous'}
          />
          <div className="w-1/3 h-full" />
          <div
            className="w-1/3 h-full cursor-pointer"
            onClick={nextStorySlide}
            title={isFa ? 'بعدی' : 'Next'}
          />
        </div>

        {/* Navigation Arrow Icons */}
        <button
          onClick={prevStorySlide}
          className="absolute left-2 top-1/2 -translate-y-1/2 z-20 w-8 h-8 rounded-full bg-black/50 border border-white/10 flex items-center justify-center text-white hover:bg-black/80"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>
        <button
          onClick={nextStorySlide}
          className="absolute right-2 top-1/2 -translate-y-1/2 z-20 w-8 h-8 rounded-full bg-black/50 border border-white/10 flex items-center justify-center text-white hover:bg-black/80"
        >
          <ChevronRight className="w-5 h-5" />
        </button>

        {/* Bottom Details & Interaction Sheet */}
        <div className="relative z-20 p-4 bg-gradient-to-t from-[#090A0D] via-[#090A0D]/95 to-transparent pt-8">
          {/* Clinical Badge */}
          <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-[#E5C378]/20 border border-[#E5C378]/40 text-[#E5C378] text-[11px] font-semibold mb-2">
            <Sparkles className="w-3 h-3" />
            <span>{isFa ? currentSlide.clinicalTagFa : currentSlide.clinicalTagEn}</span>
          </div>

          <h3 className="text-base font-bold text-white mb-1">
            {isFa ? currentSlide.titleFa : currentSlide.titleEn}
          </h3>

          <p className="text-xs text-[#9CA3AF] mb-4 leading-relaxed line-clamp-3">
            {isFa ? currentSlide.treatmentDetailsFa : currentSlide.treatmentDetailsEn}
          </p>

          {/* Direct CTA */}
          <button
            onClick={() => {
              closeModal();
              openBooking();
            }}
            className="w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] text-[#090A0D] font-bold text-xs shadow-lg mb-3 cursor-pointer active:scale-98"
          >
            <Calendar className="w-4 h-4" />
            <span>{isFa ? 'رزرو سوئیت یا ویزیت مشابه' : 'Book Similar Treatment/Suite'}</span>
          </button>

          {/* Quick Doctor Inquiry Bar */}
          <form onSubmit={handleSendInquiry} className="flex items-center gap-2">
            <input
              type="text"
              value={inquiryText}
              onChange={(e) => setInquiryText(e.target.value)}
              placeholder={isFa ? 'ارسال سوال به پزشک یا مرکز...' : 'Ask doctor a question...'}
              className="flex-1 bg-white/10 border border-white/20 rounded-xl px-3 py-2 text-xs text-white placeholder-white/50 focus:outline-none focus:border-[#E5C378]"
            />
            <button
              type="submit"
              className="p-2 rounded-xl bg-[#E5C378] text-[#090A0D] hover:bg-[#FBF4E2] transition-colors cursor-pointer"
            >
              <Send className="w-4 h-4" />
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};
