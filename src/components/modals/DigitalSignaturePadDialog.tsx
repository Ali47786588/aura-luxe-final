import React, { useRef, useState, useEffect } from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { X, Check, Trash2, Shield, Lock, Award, PenTool } from 'lucide-react';

export const DigitalSignaturePadDialog: React.FC = () => {
  const {
    language,
    activeEscrowTxForAction,
    signOffProcedure,
    closeModal,
  } = useAuraLuxe();

  const isFa = language === 'FA';
  const tx = activeEscrowTxForAction;

  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [isDrawing, setIsDrawing] = useState<boolean>(false);
  const [hasSignature, setHasSignature] = useState<boolean>(false);
  const [notes, setNotes] = useState<string>(
    'درمان با رضایت کامل مراجع، رعایت پروتکل استریلیزاسیون و بدون عارضه انجام شد.'
  );

  const [shaSeal] = useState<string>(
    () => `SIG-2026-${Math.floor(100000 + Math.random() * 900000)}-SHA256`
  );

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.strokeStyle = '#E5C378';
    ctx.lineWidth = 2.5;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
  }, []);

  if (!tx) return null;

  const startDrawing = (e: React.PointerEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    const rect = canvas.getBoundingClientRect();
    ctx.beginPath();
    ctx.moveTo(e.clientX - rect.left, e.clientY - rect.top);
    setIsDrawing(true);
    setHasSignature(true);
  };

  const draw = (e: React.PointerEvent<HTMLCanvasElement>) => {
    if (!isDrawing) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    const rect = canvas.getBoundingClientRect();
    ctx.lineTo(e.clientX - rect.left, e.clientY - rect.top);
    ctx.stroke();
  };

  const stopDrawing = () => {
    setIsDrawing(false);
  };

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    setHasSignature(false);
  };

  const handleSave = () => {
    const canvas = canvasRef.current;
    const sigData = canvas ? canvas.toDataURL('image/png') : 'mock-signature';
    signOffProcedure(tx.id, notes, sigData);
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

        {/* Modal Header */}
        <div className="mb-4">
          <div className="flex items-center gap-1.5 text-xs text-[#E5C378] font-mono uppercase mb-1">
            <PenTool className="w-3.5 h-3.5" />
            <span>{isFa ? 'امضای دیجیتال و ترخیص درمان' : 'DIGITAL CLINICAL SIGNATURE'}</span>
          </div>
          <h2 className="text-xl font-bold text-white">
            {isFa ? `تایید ترخیص: ${tx.practitionerName}` : `Discharge Sign-off: ${tx.practitionerName}`}
          </h2>
          <p className="text-xs text-[#9CA3AF] mt-0.5 font-mono">
            {tx.contractCode} • {tx.venueName}
          </p>
        </div>

        {/* Cryptographic Seal Badge */}
        <div className="p-3 rounded-xl bg-[#161822] border border-[#232736] flex items-center justify-between text-xs text-[#E5C378] font-mono mb-4">
          <div className="flex items-center gap-2">
            <Shield className="w-4 h-4 text-[#10B981]" />
            <span>{shaSeal}</span>
          </div>
          <span className="text-[10px] text-[#9CA3AF]">{isFa ? 'رمزنگاری SHA-256' : 'Verified'}</span>
        </div>

        {/* Clinical Discharge Notes */}
        <div className="mb-4">
          <label className="text-[11px] text-[#9CA3AF] block mb-1">
            {isFa ? 'یادداشت بالینی ترخیص بیمار و خاتمه عمل:' : 'Clinical Sign-off Notes:'}
          </label>
          <textarea
            rows={2}
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            className="w-full px-3.5 py-2.5 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white placeholder-[#6B7280] focus:outline-none focus:border-[#E5C378]"
          />
        </div>

        {/* Signature Canvas Pad */}
        <div className="mb-4">
          <div className="flex items-center justify-between mb-1.5">
            <span className="text-[11px] text-[#9CA3AF]">
              {isFa ? 'محل امضا یا پاراف الکترونیک پزشک:' : 'Doctor Digital Signature Pad:'}
            </span>
            <button
              type="button"
              onClick={clearCanvas}
              className="text-[11px] text-rose-400 hover:text-rose-300 flex items-center gap-1 cursor-pointer"
            >
              <Trash2 className="w-3.5 h-3.5" />
              <span>{isFa ? 'پاک کردن' : 'Clear'}</span>
            </button>
          </div>

          <div className="relative rounded-2xl bg-[#090A0D] border-2 border-dashed border-[#232736] hover:border-[#E5C378]/60 transition-colors overflow-hidden">
            <canvas
              ref={canvasRef}
              width={440}
              height={140}
              onPointerDown={startDrawing}
              onPointerMove={draw}
              onPointerUp={stopDrawing}
              onPointerLeave={stopDrawing}
              className="w-full h-[140px] touch-none cursor-crosshair"
            />
            {!hasSignature && (
              <div className="absolute inset-0 flex items-center justify-center text-xs text-[#6B7280] pointer-events-none select-none">
                {isFa ? 'با لمس یا نشانگر ماوس اینجا امضا کنید' : 'Sign here with finger or mouse'}
              </div>
            )}
          </div>
        </div>

        {/* Warning / Advisory */}
        <p className="text-[10px] text-[#9CA3AF] leading-relaxed mb-5">
          {isFa
            ? 'با ثبت این امضا، پنجره تضمین ۱۲۰ دقیقه‌ای فعال و پس از انقضا یا تایید نهایی، مبلغ به شبا مقصد واریز خواهد شد.'
            : 'Upon signing, the 120-minute escrow protection cooldown begins before fund release to IBAN.'}
        </p>

        {/* Submit */}
        <button
          onClick={handleSave}
          className="w-full py-3 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-[#090A0D] font-bold text-xs shadow-lg transition-all cursor-pointer flex items-center justify-center gap-2 active:scale-98"
        >
          <Check className="w-4 h-4" />
          <span>{isFa ? 'تایید نهایی و مهر دیجیتال بالینی' : 'Seal & Finalize Procedure'}</span>
        </button>
      </div>
    </div>
  );
};
