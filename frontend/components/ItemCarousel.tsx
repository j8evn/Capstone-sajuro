'use client';

import type { MenuItemInfo } from '@/types/saju';
import { getElementClass } from '@/types/saju';

interface ItemCarouselProps {
  items: MenuItemInfo[];
}

export default function ItemCarousel({ items }: ItemCarouselProps) {
  if (items.length === 0) return null;

  return (
    <div>
      <div
        style={{
          fontSize: 'var(--font-size-xs)',
          color: 'var(--color-text-muted)',
          marginBottom: 'var(--space-sm)',
        }}
      >
        🍽️ 추천 메뉴/아이템
      </div>
      <div className="menu-carousel">
        {items.map((item, i) => (
          <div key={i} className="menu-item-card">
            <div className="menu-item-name">{item.name}</div>
            <div className="menu-item-desc">{item.description}</div>
            <div
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
              }}
            >
              <span className="menu-item-price">₩{item.price.toLocaleString()}</span>
              <span
                className={`element-badge ${getElementClass(item.element)}`}
                style={{ fontSize: '10px' }}
              >
                {item.element}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
