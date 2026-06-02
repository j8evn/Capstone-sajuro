'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

const navItems = [
  { href: '/', icon: '🏠', label: '홈' },
  { href: '/input', icon: '📝', label: '사주입력' },
  { href: '/result', icon: '🔮', label: '분석' },
  { href: '/context', icon: '💫', label: '추천' },
];

export default function Navigation() {
  const pathname = usePathname();

  return (
    <nav className="bottom-nav">
      <div className="bottom-nav-inner">
        {navItems.map((item) => (
          <Link
            key={item.href}
            href={item.href}
            className={`nav-item ${pathname === item.href ? 'active' : ''}`}
          >
            <span className="nav-icon">{item.icon}</span>
            <span>{item.label}</span>
          </Link>
        ))}
      </div>
    </nav>
  );
}
