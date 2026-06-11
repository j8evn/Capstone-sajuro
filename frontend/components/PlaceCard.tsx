'use client';

import type { PlaceRecommendation } from '@/types/saju';
import ItemCarousel from './ItemCarousel';

interface PlaceCardProps {
  place: PlaceRecommendation;
  rank: number;
  isExpanded: boolean;
  onToggle: () => void;
  animDelay?: number;
}

const CATEGORY_BG: Record<string, string> = {
  cafe: 'linear-gradient(135deg, #3e2723 0%, #4e342e 100%)',
  restaurant: 'linear-gradient(135deg, #1b5e20 0%, #2e7d32 100%)',
  park: 'linear-gradient(135deg, #1a237e 0%, #283593 100%)',
  culture: 'linear-gradient(135deg, #4a148c 0%, #6a1b9a 100%)',
  shopping: 'linear-gradient(135deg, #b71c1c 0%, #c62828 100%)',
};

function ScoreLine({ label, value, weight }: { label: string; value: number; weight: string }) {
  return (
    <div>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          fontSize: 'var(--font-size-xs)',
          marginBottom: '3px',
        }}
      >
        <span style={{ color: 'var(--color-text-secondary)' }}>{label}</span>
        <span style={{ color: 'var(--color-text-muted)' }}>
          {value} <span style={{ fontSize: '9px' }}>({weight})</span>
        </span>
      </div>
      <div className="score-bar" style={{ height: '3px' }}>
        <div className="score-bar-fill" style={{ width: `${value}%` }} />
      </div>
    </div>
  );
}

export default function PlaceCard({
  place,
  rank,
  isExpanded,
  onToggle,
  animDelay = 0,
}: PlaceCardProps) {
  return (
    <div
      className="place-card animate-fade-in-up"
      style={{ animationDelay: `${animDelay}s`, opacity: 0 }}
    >
      {/* Image Area */}
      <div
        className="place-card-image"
        style={{ background: CATEGORY_BG[place.category] || CATEGORY_BG.cafe }}
      >
        <span>{place.categoryEmoji}</span>
        <div className="place-card-score">
          #{rank} · {place.matchScore}점
        </div>
      </div>

      {/* Body */}
      <div className="place-card-body">
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: 'var(--space-sm)',
            marginBottom: 'var(--space-xs)',
          }}
        >
          <h3 className="place-card-name">{place.name}</h3>
        </div>
        <p className="place-card-desc">{place.description}</p>

        {/* Score Bar */}
        <div style={{ marginBottom: 'var(--space-md)' }}>
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              marginBottom: '4px',
            }}
          >
            <span style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)' }}>
              매칭 점수
            </span>
            <span
              style={{
                fontSize: 'var(--font-size-xs)',
                fontWeight: 600,
                color: 'var(--color-gold)',
              }}
            >
              {place.matchScore}/100
            </span>
          </div>
          <div className="score-bar">
            <div className="score-bar-fill" style={{ width: `${place.matchScore}%` }} />
          </div>
        </div>

        {/* Reason */}
        <div className="place-card-reason">✨ {place.reasonText}</div>

        {/* Tags */}
        <div className="place-card-meta">
          {place.matchedTags.map((tag, i) => (
            <span key={i} className="tag">
              {tag}
            </span>
          ))}
        </div>

        {/* Expandable */}
        <button className="collapsible-trigger" onClick={onToggle}>
          <span>상세 정보 보기</span>
          <span
            style={{
              transform: isExpanded ? 'rotate(180deg)' : 'rotate(0)',
              transition: 'transform 0.3s',
            }}
          >
            ▼
          </span>
        </button>

        <div className={`collapsible-content ${isExpanded ? 'open' : ''}`}>
          {/* Score Breakdown */}
          <div style={{ padding: 'var(--space-md) 0' }}>
            <div
              style={{
                fontSize: 'var(--font-size-xs)',
                color: 'var(--color-text-muted)',
                marginBottom: 'var(--space-sm)',
              }}
            >
              점수 분석
            </div>
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                gap: 'var(--space-sm)',
              }}
            >
              <ScoreLine label="🔮 사주 적합도" value={place.scoreBreakdown.sajuScore} weight="35%" />
              <ScoreLine label="🌤️ 날씨 적합도" value={place.scoreBreakdown.weatherScore} weight="20%" />
              <ScoreLine label="💭 기분 적합도" value={place.scoreBreakdown.moodScore} weight="25%" />
              <ScoreLine label="🕐 시간 적합도" value={place.scoreBreakdown.timeScore} weight="10%" />
            </div>
          </div>

          {/* Menu Items via ItemCarousel */}
          {place.menuItems.length > 0 && (
            <div style={{ paddingBottom: 'var(--space-md)' }}>
              <ItemCarousel items={place.menuItems} />
            </div>
          )}

          {/* Address */}
          <div
            style={{
              fontSize: 'var(--font-size-xs)',
              color: 'var(--color-text-muted)',
              padding: 'var(--space-sm) 0',
              borderTop: '1px solid rgba(255,255,255,0.06)',
            }}
          >
            📍 {place.address}
          </div>
        </div>
      </div>
    </div>
  );
}
