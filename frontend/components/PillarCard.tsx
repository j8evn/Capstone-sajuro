'use client';

import type { PillarDetail } from '@/types/saju';
import { getElementClass } from '@/types/saju';

interface PillarCardProps {
  label: string;
  data: PillarDetail;
  animDelay?: number;
}

export default function PillarCard({ label, data, animDelay = 0 }: PillarCardProps) {
  return (
    <div
      className="pillar-card animate-fade-in-up"
      style={{
        animationDelay: `${animDelay}s`,
        opacity: 0,
        '--pillar-color': data.stemElementColor,
      } as React.CSSProperties}
    >
      <div className="pillar-label">{label}</div>
      <div className="pillar-hanja" style={{ color: data.stemElementColor }}>
        {data.stemHanja}
      </div>
      <div className="pillar-hanja" style={{ color: data.branchElementColor }}>
        {data.branchHanja}
      </div>
      <div className="pillar-korean">
        {data.stemKorean}{data.branchKorean}
      </div>
      <div className="pillar-element">
        <span className={`element-badge ${getElementClass(data.stemElement)}`}>
          {data.stemElement}
        </span>
      </div>
      <div style={{ fontSize: '11px', color: 'var(--color-text-muted)', marginTop: '4px' }}>
        {data.yinYang} · {data.animal}
      </div>
    </div>
  );
}
