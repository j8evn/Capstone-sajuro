'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import type { RecommendResponse } from '@/types/saju';
import { getElementClass } from '@/types/saju';
import PlaceCard from '@/components/PlaceCard';
import LoadingSpinner from '@/components/LoadingSpinner';

export default function RecommendationsPage() {
  const router = useRouter();
  const [data, setData] = useState<RecommendResponse | null>(null);
  const [expandedId, setExpandedId] = useState<string | null>(null);

  useEffect(() => {
    const stored = sessionStorage.getItem('recommendations');
    if (stored) {
      setData(JSON.parse(stored));
    } else {
      router.push('/context');
    }
  }, [router]);

  if (!data) {
    return <LoadingSpinner text="추천 결과를 불러오는 중..." />;
  }

  return (
    <div className="page">
      <div className="container">
        <div className="page-header">
          <h1 className="page-title animate-fade-in-up stagger-1">맞춤 추천</h1>
          <p className="page-subtitle animate-fade-in-up stagger-2">
            {data.moodEmoji} {data.weather.description} · {Math.round(data.weather.temperature)}° · {data.contextSummary.timeOfDay}
          </p>
        </div>

        {/* Context Summary */}
        <section className="section animate-fade-in-up stagger-2">
          <div className="glass-card" style={{ display: 'flex', justifyContent: 'space-around', textAlign: 'center' }}>
            <div>
              <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginBottom: '4px' }}>운세</div>
              <div style={{ fontSize: 'var(--font-size-xl)', fontWeight: 700, color: 'var(--color-gold)' }}>
                {data.contextSummary.fortuneScore}
              </div>
            </div>
            <div>
              <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginBottom: '4px' }}>필요한 기운</div>
              <span className={`element-badge ${getElementClass(data.contextSummary.neededElement)}`}>
                {data.contextSummary.neededElement}
              </span>
            </div>
            <div>
              <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginBottom: '4px' }}>날씨 기운</div>
              <span className={`element-badge ${getElementClass(data.contextSummary.weatherElement)}`}>
                {data.contextSummary.weatherElement}
              </span>
            </div>
          </div>
        </section>

        {/* Recommendations */}
        <section className="section">
          <h2 className="section-title animate-fade-in-up stagger-3">
            <span className="section-title-icon">📍</span> 추천 장소 Top {data.recommendations.length}
          </h2>

          {data.recommendations.map((place, index) => (
            <PlaceCard
              key={place.id}
              place={place}
              rank={index + 1}
              isExpanded={expandedId === place.id}
              onToggle={() => setExpandedId(expandedId === place.id ? null : place.id)}
              animDelay={0.3 + index * 0.1}
            />
          ))}
        </section>
      </div>
    </div>
  );
}
