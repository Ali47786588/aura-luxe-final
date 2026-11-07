import React, { useState } from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { SupportTicket } from '../../types';
import { X, LifeBuoy, Send, MessageSquare, Clock, User, ShieldCheck } from 'lucide-react';

export const SupportTicketingModal: React.FC = () => {
  const { language, tickets, replyToTicket, closeModal } = useAuraLuxe();
  const isFa = language === 'FA';

  const [selectedTicketId, setSelectedTicketId] = useState<string>(tickets[0]?.id || '');
  const [replyText, setReplyText] = useState<string>('');

  const activeTicket = tickets.find((t) => t.id === selectedTicketId) || tickets[0];

  const handleSendReply = (e: React.FormEvent) => {
    e.preventDefault();
    if (!replyText.trim() || !activeTicket) return;
    replyToTicket(activeTicket.id, replyText);
    setReplyText('');
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 overflow-y-auto animate-fadeIn">
      <div className="relative w-full max-w-3xl bg-[#11131A] border border-[#E5C378]/40 rounded-3xl p-6 sm:p-8 shadow-2xl my-8">
        {/* Close Button */}
        <button
          onClick={closeModal}
          className="absolute top-5 left-5 w-8 h-8 rounded-full bg-[#161822] border border-[#232736] flex items-center justify-center text-[#9CA3AF] hover:text-white hover:bg-[#232736] transition-colors cursor-pointer"
        >
          <X className="w-4 h-4" />
        </button>

        {/* Modal Header */}
        <div className="mb-6">
          <div className="flex items-center gap-1.5 text-xs text-[#38BDF8] font-mono uppercase mb-1">
            <LifeBuoy className="w-3.5 h-3.5" />
            <span>{isFa ? 'پشتیبانی فنی و استعلام اسکرو' : 'SUPPORT DESK & INQUIRIES'}</span>
          </div>
          <h2 className="text-xl font-bold text-white">
            {isFa ? 'تیکت‌های پشتیبانی مراکز و استعلام پایا' : 'Clinical & Settlement Support'}
          </h2>
        </div>

        {/* 2-Column Layout: Ticket List & Conversation View */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {/* Ticket List */}
          <div className="space-y-2 md:border-l border-[#232736] pl-2 max-h-[420px] overflow-y-auto">
            {tickets.map((t) => {
              const isSelected = t.id === selectedTicketId;
              return (
                <button
                  key={t.id}
                  onClick={() => setSelectedTicketId(t.id)}
                  className={`w-full p-3 rounded-2xl border text-right transition-all cursor-pointer ${
                    isSelected
                      ? 'bg-[#E5C378]/10 border-[#E5C378]'
                      : 'bg-[#161822] border-[#232736] hover:border-white/20'
                  }`}
                >
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-[10px] font-mono text-[#E5C378]">{t.ticketNumber}</span>
                    <span
                      className={`text-[9px] font-bold px-1.5 py-0.5 rounded font-mono ${
                        t.status === 'answered'
                          ? 'bg-emerald-500/20 text-emerald-400'
                          : 'bg-amber-500/20 text-amber-400'
                      }`}
                    >
                      {t.status.toUpperCase()}
                    </span>
                  </div>
                  <div className="text-xs font-bold text-white truncate">{t.subject}</div>
                  <div className="text-[10px] text-[#9CA3AF] mt-1">{t.senderName}</div>
                </button>
              );
            })}
          </div>

          {/* Conversation Detail & Reply Composer */}
          <div className="md:col-span-2 flex flex-col justify-between bg-[#161822] border border-[#232736] rounded-2xl p-4 min-h-[380px]">
            {activeTicket ? (
              <>
                {/* Header Info */}
                <div className="pb-3 border-b border-[#232736] mb-3">
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-bold text-white">{activeTicket.subject}</span>
                    <span className="font-mono text-[#E5C378] text-[11px]">
                      {activeTicket.ticketNumber}
                    </span>
                  </div>
                  <div className="text-[11px] text-[#9CA3AF] mt-0.5">
                    {activeTicket.venueName} • {activeTicket.createdAt}
                  </div>
                </div>

                {/* Messages Thread */}
                <div className="space-y-3 flex-1 overflow-y-auto max-h-[240px] pr-1 mb-4">
                  {activeTicket.messages.map((m) => (
                    <div
                      key={m.id}
                      className={`p-3 rounded-xl text-xs max-w-[85%] ${
                        m.isStaff
                          ? 'bg-[#E5C378]/15 border border-[#E5C378]/30 mr-auto text-white'
                          : 'bg-[#090A0D] border border-[#232736] ml-auto text-white'
                      }`}
                    >
                      <div className="flex items-center justify-between text-[10px] mb-1">
                        <span className={m.isStaff ? 'text-[#E5C378] font-bold' : 'text-[#9CA3AF]'}>
                          {m.senderName}
                        </span>
                        <span className="text-[#6B7280] font-mono mr-2">{m.timestamp}</span>
                      </div>
                      <p className="leading-relaxed text-xs">{m.content}</p>
                    </div>
                  ))}
                </div>

                {/* Reply Bar */}
                <form onSubmit={handleSendReply} className="flex items-center gap-2 pt-2 border-t border-[#232736]">
                  <input
                    type="text"
                    value={replyText}
                    onChange={(e) => setReplyText(e.target.value)}
                    placeholder={isFa ? 'پاسخ یا استعلام خود را بنویسید...' : 'Type your reply...'}
                    className="flex-1 px-3.5 py-2.5 rounded-xl bg-[#090A0D] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
                  />
                  <button
                    type="submit"
                    className="px-4 py-2.5 rounded-xl bg-[#E5C378] hover:bg-[#FBF4E2] text-[#090A0D] font-bold text-xs flex items-center gap-1.5 transition-colors cursor-pointer"
                  >
                    <Send className="w-3.5 h-3.5" />
                    <span>{isFa ? 'ارسال' : 'Send'}</span>
                  </button>
                </form>
              </>
            ) : (
              <div className="text-center py-20 text-xs text-[#9CA3AF]">
                {isFa ? 'تیکتی انتخاب نشده است.' : 'No ticket selected.'}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
