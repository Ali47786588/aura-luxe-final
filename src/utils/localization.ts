import { Language } from '../types';

const persianDigits = ['۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'];

export function toPersianDigits(input: string | number): string {
  const str = String(input);
  let res = '';
  for (let i = 0; i < str.length; i++) {
    const ch = str[i];
    if (ch >= '0' && ch <= '9') {
      res += persianDigits[Number(ch)];
    } else {
      res += ch;
    }
  }
  return res;
}

export function formatPrice(amount: number, lang: Language): string {
  const formatted = new Intl.NumberFormat('en-US').format(amount);
  if (lang === 'FA') {
    return `${toPersianDigits(formatted)} تومان`;
  }
  return `${formatted} Toman`;
}

export function formatNumber(number: number | string, lang: Language): string {
  const str = String(number);
  return lang === 'FA' ? toPersianDigits(str) : str;
}
