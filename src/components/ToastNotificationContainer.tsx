import React from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { CheckCircle2, AlertTriangle, Info, XCircle, X } from 'lucide-react';

export const ToastNotificationContainer: React.FC = () => {
  const { toasts, dismissToast } = useAuraLuxe();

  if (toasts.length === 0) return null;

  return (
    <div className="fixed top-4 left-1/2 -translate-x-1/2 z-50 flex flex-col items-center gap-2 w-full max-w-md px-4 pointer-events-none">
      {toasts.map((toast) => {
        const getStyles = () => {
          switch (toast.type) {
            case 'SUCCESS':
              return {
                bg: 'bg-[#11131A] border-emerald-500/50 text-white',
                icon: <CheckCircle2 className="w-5 h-5 text-emerald-400 flex-shrink-0" />,
                titleColor: 'text-emerald-400',
              };
            case 'WARNING':
              return {
                bg: 'bg-[#11131A] border-amber-500/50 text-white',
                icon: <AlertTriangle className="w-5 h-5 text-amber-400 flex-shrink-0" />,
                titleColor: 'text-amber-400',
              };
            case 'ERROR':
              return {
                bg: 'bg-[#11131A] border-rose-500/50 text-white',
                icon: <XCircle className="w-5 h-5 text-rose-400 flex-shrink-0" />,
                titleColor: 'text-rose-400',
              };
            case 'INFO':
            default:
              return {
                bg: 'bg-[#11131A] border-[#E5C378]/50 text-white',
                icon: <Info className="w-5 h-5 text-[#E5C378] flex-shrink-0" />,
                titleColor: 'text-[#E5C378]',
              };
          }
        };

        const { bg, icon, titleColor } = getStyles();

        return (
          <div
            key={toast.id}
            className={`pointer-events-auto w-full p-3.5 rounded-2xl border shadow-2xl backdrop-blur-md flex items-start justify-between gap-3 animate-slideDown ${bg}`}
          >
            <div className="flex items-start gap-2.5">
              {icon}
              <div>
                <h4 className={`text-xs font-bold ${titleColor} leading-snug`}>
                  {toast.title}
                </h4>
                <p className="text-xs text-[#9CA3AF] mt-0.5 leading-relaxed">
                  {toast.message}
                </p>
              </div>
            </div>

            <button
              onClick={() => dismissToast(toast.id)}
              className="text-[#6B7280] hover:text-white p-1 cursor-pointer flex-shrink-0"
            >
              <X className="w-3.5 h-3.5" />
            </button>
          </div>
        );
      })}
    </div>
  );
};
