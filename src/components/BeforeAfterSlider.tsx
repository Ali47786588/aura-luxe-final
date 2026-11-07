import React, { useState, useRef } from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { BeforeAfterCase } from '../types';
import { Sparkles, Calendar, Layers, Activity } from 'lucide-react';

export const BeforeAfterSlider: React.FC = () => {
  const { language, beforeAfterCases, openBooking, venues } = useAuraLuxe();
  const isFa = language === 'FA';

  const [selectedCaseId, setSelectedCaseId] = useState<string>(
    beforeAfterCases[0]?.id || ''
  );
  const [splitFraction, setSplitFraction] = useState<number>(0.5);
  const containerRef = useRef<HTMLDivElement | null>(null);
  const isDraggingRef = useRef<boolean>(false);

  const currentCase =
    beforeAfterCases.find((c) => c.id === selectedCaseId) || beforeAfterCases[0];

  const handlePointerDown = (e: React.PointerEvent) => {
    isDraggingRef.current = true;
    updateSplit(e.clientX);
    window.addEventListener('pointermove', handlePointerMove);
    window.addEventListener('pointerup', handlePointerUp);
  };

  const handlePointerMove = (e: MouseEvent) => {
    if (!isDraggingRef.current) return;
    updateSplit(e.clientX);
  };

  const handlePointerUp = () => {
    isDraggingRef.current = false;
    window.removeEventListener('pointermove', handlePointerMove);
    window.removeEventListener('pointerup', handlePointerUp);
  };

  const updateSplit = (clientX: number) => {
    if (!containerRef.current) return;
    const rect = containerRef.current.getBoundingClientRect();
    const x = clientX - rect.left;
    const fraction = Math.max(0.05, Math.min(0.95, x / rect.width));
    setSplitFraction(fraction);
  };

  return (
    <section className="mx-4 lg:mx-8 py-6">
      <div className="rounded-3xl bg-[#11131A] border border-[#232736] p-6 lg:p-8">
        {/* Header & Tabs */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
          <div>
            <div className="flex items-center gap-2 text-[#E5C378] text-xs font-bold font-mono tracking-wider uppercase mb-1">
              <Sparkles className="w-4 h-4" />
              <span>{isFa ? 'نتایج بالینی و ترنسفورمیشن' : 'CINEMATIC OUTCOMES'}</span>
            </div>
            <h2 className="text-xl sm:text-2xl font-bold text-white">
              {isFa ? 'مقایسه قبل و بعد درمان (اسلایدر تعاملی)' : 'Interactive Before / After Showcase'}
            </h2>
          </div>

          {/* Case switcher tabs */}
          <div className="flex items-center gap-1.5 p-1 rounded-xl bg-[#090A0D] border border-[#232736]">
            {beforeAfterCases.map((c) => {
              const isSelected = c.id === currentCase.id;
              return (
                <button
                  key={c.id}
                  onClick={() => {
                    setSelectedCaseId(c.id);
                    setSplitFraction(0.5);
                  }}
                  className={`px-3.5 py-1.5 rounded-lg text-xs font-semibold transition-all cursor-pointer ${
                    isSelected
                      ? 'bg-[#E5C378] text-[#090A0D] shadow'
                      : 'text-[#9CA3AF] hover:text-white'
                  }`}
                >
                  {isFa ? c.titleFa.split(' ').slice(0, 3).join(' ') : c.titleEn.split(' ').slice(0, 3).join(' ')}
                </button>
              );
            })}
          </div>
        </div>

        {/* Dual-layer interactive image comparison */}
        <div
          ref={containerRef}
          onPointerDown={handlePointerDown}
          className="relative h-72 sm:h-96 w-full rounded-2xl overflow-hidden cursor-ew-resize select-none border border-[#232736] bg-[#090A0D] shadow-inner mb-6"
        >
          {/* Layer 2: AFTER Image (Underneath, full color) */}
          <img
            src={currentCase.image}
            alt="After outcome"
            className="absolute inset-0 w-full h-full object-cover"
          />

          {/* Layer 1: BEFORE Image (Clipped, sepia/desaturated filter) */}
          <div
            className="absolute inset-y-0 left-0 overflow-hidden"
            style={{ width: `${splitFraction * 100}%` }}
          >
            <div
              className="absolute inset-0"
              style={{
                width: containerRef.current ? `${containerRef.current.offsetWidth}px` : '100%',
              }}
            >
              <img
                src={currentCase.image}
                alt="Before condition"
                className="w-full h-full object-cover filter saturate-50 contrast-85 brightness-90 sepia-25"
              />
            </div>
            {/* Dark overlay on before layer */}
            <div className="absolute inset-0 bg-black/20" />
          </div>

          {/* Before & After Badges */}
          <div className="absolute top-4 left-4 z-10 px-3 py-1 rounded-full bg-black/70 backdrop-blur-md border border-white/20 text-xs font-bold text-white uppercase tracking-wider">
            {isFa ? 'پیش از درمان' : 'BEFORE'}
          </div>
          <div className="absolute top-4 right-4 z-10 px-3 py-1 rounded-full bg-[#E5C378]/90 backdrop-blur-md border border-[#E5C378] text-xs font-bold text-[#090A0D] uppercase tracking-wider">
            {isFa ? 'نتیجه ترنسفورمیشن' : 'AFTER'}
          </div>

          {/* Divider Handle */}
          <div
            className="absolute inset-y-0 z-20 w-0.5 bg-[#E5C378] shadow-[0_0_12px_#E5C378]"
            style={{ left: `${splitFraction * 100}%` }}
          >
            <div className="absolute top-1/2 -translate-y-1/2 -translate-x-1/2 w-9 h-9 rounded-full bg-[#E5C378] text-[#090A0D] flex items-center justify-center font-bold text-xs shadow-xl border-2 border-[#090A0D]">
              ↔
            </div>
          </div>
        </div>

        {/* Case Info & Clinical Metrics */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 pt-2">
          {/* Summary */}
          <div className="lg:col-span-2">
            <h3 className="text-lg font-bold text-white mb-1">
              {isFa ? currentCase.titleFa : currentCase.titleEn}
            </h3>
            <p className="text-xs text-[#E5C378] mb-3">
              {isFa ? currentCase.clinicNameFa : currentCase.clinicNameEn} •{' '}
              {isFa ? currentCase.doctorNameFa : currentCase.doctorNameEn}
            </p>
            <p className="text-xs text-[#9CA3AF] leading-relaxed mb-4">
              {isFa ? currentCase.treatmentFa : currentCase.treatmentEn}
            </p>
            <p className="text-xs text-[#6B7280] italic leading-relaxed">
              {isFa ? currentCase.notesFa : currentCase.notesEn}
            </p>
          </div>

          {/* Metrics Column & CTA */}
          <div className="flex flex-col justify-between">
            <div className="grid grid-cols-2 gap-2 mb-4">
              {(isFa ? currentCase.metricsFa : currentCase.metricsEn).map((m, idx) => (
                <div
                  key={idx}
                  className="p-2.5 rounded-xl bg-[#161822] border border-[#232736]"
                >
                  <div className="text-[10px] text-[#9CA3AF] mb-0.5">{m.label}</div>
                  <div className="text-xs font-bold text-white font-mono">{m.value}</div>
                </div>
              ))}
            </div>

            <button
              onClick={() => {
                const targetCat = currentCase.category;
                const v = venues.find((vn) => vn.category === targetCat) || venues[0];
                openBooking(v);
              }}
              className="w-full py-3 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-xs font-bold text-[#090A0D] shadow-md transition-all cursor-pointer flex items-center justify-center gap-2 active:scale-95"
            >
              <Calendar className="w-4 h-4" />
              <span>{isFa ? 'رزرو سوئیت یا خدمات مشابه' : 'Book Similar Suite'}</span>
            </button>
          </div>
        </div>
      </div>
    </section>
  );
};
