import React from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { Sparkles } from 'lucide-react';

export const InstagramStoryGallery: React.FC = () => {
  const { language, storyHighlights, openStoryHighlight } = useAuraLuxe();
  const isFa = language === 'FA';

  return (
    <div className="mx-4 lg:mx-8 py-3">
      {/* Header */}
      <div className="flex items-center justify-between mb-3 px-1">
        <div className="flex items-center gap-2">
          <Sparkles className="w-4 h-4 text-[#E5C378]" />
          <h2 className="text-xs font-bold tracking-wider text-[#E5C378] uppercase font-mono">
            {isFa ? 'استوری و نتایج بالینی زنده' : 'CLINICAL CASE STORIES'}
          </h2>
        </div>
        <span className="text-[11px] text-[#6B7280]">
          {isFa ? 'لمس جهت مشاهده پرونده‌ها' : 'Tap to view live outcomes'}
        </span>
      </div>

      {/* Story Bubbles Row */}
      <div className="flex items-center gap-4 overflow-x-auto pb-2 scrollbar-none">
        {storyHighlights.map((hl) => (
          <button
            key={hl.id}
            onClick={() => openStoryHighlight(hl)}
            className="flex flex-col items-center gap-2 flex-shrink-0 group cursor-pointer"
          >
            {/* Story Ring */}
            <div className="relative p-[2.5px] rounded-full bg-gradient-to-tr from-[#E5C378] via-[#FBF4E2] to-[#C5A059] group-hover:scale-105 transition-transform shadow-[0_0_15px_rgba(229,195,120,0.3)]">
              <div className="w-18 h-18 sm:w-20 sm:h-20 rounded-full overflow-hidden bg-[#11131A] p-0.5 border-2 border-[#090A0D]">
                <img
                  src={hl.coverImage}
                  alt={isFa ? hl.titleFa : hl.titleEn}
                  className="w-full h-full object-cover rounded-full group-hover:scale-110 transition-transform duration-300"
                />
              </div>
              <div className="absolute -bottom-1 -right-1 px-1.5 py-0.5 bg-[#090A0D] border border-[#E5C378]/50 rounded-full text-[9px] font-bold text-[#E5C378] font-mono">
                {hl.slides.length}
              </div>
            </div>
            {/* Title */}
            <span className="text-xs font-medium text-[#F9FAFB] group-hover:text-[#E5C378] transition-colors max-w-[80px] text-center truncate">
              {isFa ? hl.titleFa : hl.titleEn}
            </span>
          </button>
        ))}
      </div>
    </div>
  );
};
