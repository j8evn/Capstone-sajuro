'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import type { SajuProfile, PillarDetail } from '@/types/saju';
import { getElementClass } from '@/types/saju';

export default function ResultPage() {
  const router = useRouter();
  const [profile, setProfile] = useState<SajuProfile | null>(null);

  useEffect(() => {
    const stored = sessionStorage.getItem('sajuProfile');
    if (stored) {
      setProfile(JSON.parse(stored));
    } else {
      router.push('/input');
    }
  }, [router]);

  if (!profile) {
    return (
      <div className="loading-container">
        <div className="loading-spinner" />
        <p className="loading-text">사주 데이터를 불러오는 중...</p>
      </div>
    );
  }

  const { fourPillars, analysis, marketingVariables, dailyFortune } = profile;
  const pillars: { label: string; data: PillarDetail }[] = [
    { label: '시주(時柱)', data: fourPillars.hour },
    { label: '일주(日柱)', data: fourPillars.day },
    { label: '월주(月柱)', data: fourPillars.month },
    { label: '년주(年柱)', data: fourPillars.year },
  ];

  // Calculate radar chart values
  const elements = ['목', '화', '토', '금', '수'];
  const maxElementCount = Math.max(...elements.map(e => analysis.elementDistribution[e] || 0), 1);

  return (
    <div className="page">
      <div className="container">
        <div className="page-header">
          <h1 className="page-title animate-fade-in-up stagger-1">사주 분석 결과</h1>
          <p className="page-subtitle animate-fade-in-up stagger-2">
            {profile.birthInfo.year}년 {profile.birthInfo.month}월 {profile.birthInfo.day}일 {profile.birthInfo.hour}시생
          </p>
        </div>

        {/* Four Pillars Visualization */}
        <section className="section animate-fade-in-up stagger-2">
          <h2 className="section-title">
            <span className="section-title-icon">📜</span> 사주 명반
          </h2>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 'var(--space-sm)' }}>
            {pillars.map(({ label, data }, i) => (
              <div
                key={label}
                className="pillar-card animate-fade-in-up"
                style={{
                  animationDelay: `${0.3 + i * 0.15}s`,
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
            ))}
          </div>
          <div style={{
            textAlign: 'center',
            marginTop: 'var(--space-md)',
            padding: 'var(--space-sm) var(--space-md)',
            background: 'rgba(212, 165, 116, 0.08)',
            borderRadius: 'var(--radius-md)',
            fontSize: 'var(--font-size-sm)',
          }}>
            <span style={{ color: 'var(--color-text-muted)' }}>일간(나): </span>
            <span style={{ color: analysis.dayMasterElementColor, fontWeight: 700 }}>
              {analysis.dayMaster}
            </span>
            <span style={{ color: 'var(--color-text-muted)' }}> — </span>
            <span className={`element-badge ${getElementClass(analysis.dayMasterElement)}`}>
              {analysis.dayMasterElement}({analysis.dayMasterElement === '목' ? '木' : analysis.dayMasterElement === '화' ? '火' : analysis.dayMasterElement === '토' ? '土' : analysis.dayMasterElement === '금' ? '金' : '水'})
            </span>
          </div>
        </section>

        {/* Five Elements Distribution */}
        <section className="section animate-fade-in-up stagger-3">
          <h2 className="section-title">
            <span className="section-title-icon">🌊</span> 오행 분포
          </h2>
          <div className="glass-card">
            <RadarChart elements={elements} distribution={analysis.elementDistribution} max={maxElementCount} />
            <div style={{ display: 'flex', justifyContent: 'center', gap: 'var(--space-md)', flexWrap: 'wrap', marginTop: 'var(--space-md)' }}>
              {elements.map(el => (
                <div key={el} style={{ textAlign: 'center' }}>
                  <span className={`element-badge ${getElementClass(el)}`}>
                    {el} {analysis.elementDistribution[el] || 0}
                  </span>
                </div>
              ))}
            </div>
            <div style={{
              textAlign: 'center',
              marginTop: 'var(--space-lg)',
              padding: 'var(--space-sm)',
              borderTop: '1px solid rgba(255,255,255,0.06)',
            }}>
              <span style={{ fontSize: 'var(--font-size-sm)', color: 'var(--color-text-secondary)' }}>
                보충이 필요한 오행:
              </span>{' '}
              <span className={`element-badge ${getElementClass(analysis.neededElement)}`} style={{ fontWeight: 700 }}>
                {analysis.neededElement}
              </span>
            </div>
          </div>
        </section>

        {/* Daily Fortune */}
        <section className="section animate-fade-in-up stagger-4">
          <h2 className="section-title">
            <span className="section-title-icon">🌟</span> 오늘의 운세
          </h2>
          <div className="glass-card" style={{ textAlign: 'center' }}>
            <div className="score-gauge">
              <svg width="120" height="120" viewBox="0 0 120 120">
                <circle cx="60" cy="60" r="50" className="score-gauge-bg" />
                <circle
                  cx="60" cy="60" r="50"
                  className="score-gauge-fill"
                  style={{
                    stroke: dailyFortune.score >= 70 ? 'var(--color-emerald)' : dailyFortune.score >= 40 ? 'var(--color-gold)' : 'var(--color-fire)',
                    strokeDasharray: `${2 * Math.PI * 50}`,
                    strokeDashoffset: `${2 * Math.PI * 50 * (1 - dailyFortune.score / 100)}`,
                  }}
                />
              </svg>
              <div className="score-gauge-text">
                <div className="score-gauge-value">{dailyFortune.score}</div>
                <div className="score-gauge-label">/ 100</div>
              </div>
            </div>
            <p style={{ marginTop: 'var(--space-md)', color: 'var(--color-text-secondary)', fontSize: 'var(--font-size-sm)', lineHeight: 1.6 }}>
              {dailyFortune.aiDescription || dailyFortune.description}
            </p>
            {profile.aiEnabled && (
              <div style={{ marginTop: 'var(--space-sm)', display: 'flex', justifyContent: 'center' }}>
                <span style={{
                  fontSize: '10px',
                  color: 'var(--color-purple-light)',
                  background: 'rgba(155, 89, 182, 0.1)',
                  border: '1px solid rgba(155, 89, 182, 0.2)',
                  borderRadius: 'var(--radius-full)',
                  padding: '2px 8px',
                }}>✨ Gemini AI Powered</span>
              </div>
            )}
          </div>
        </section>

        {/* Personality Keywords */}
        <section className="section animate-fade-in-up stagger-4">
          <h2 className="section-title">
            <span className="section-title-icon">🧬</span> 성격 키워드
          </h2>
          <div className="glass-card">
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 'var(--space-sm)', justifyContent: 'center' }}>
              {analysis.personalityKeywords.map((kw, i) => (
                <span key={i} className="tag tag-gold" style={{ fontSize: 'var(--font-size-sm)', padding: '6px 14px' }}>
                  {kw}
                </span>
              ))}
            </div>
            <div style={{ display: 'flex', gap: 'var(--space-lg)', justifyContent: 'center', marginTop: 'var(--space-lg)' }}>
              <InfoChip label="사교성" value={analysis.socialTendency === 'extrovert' ? '외향적' : analysis.socialTendency === 'introvert' ? '내향적' : '양향적'} />
              <InfoChip label="의사결정" value={analysis.decisionStyle === 'intuitive' ? '직관적' : analysis.decisionStyle === 'analytical' ? '분석적' : '균형적'} />
            </div>
            {analysis.personalityDescription && (
              <div style={{
                marginTop: 'var(--space-lg)',
                padding: 'var(--space-md)',
                background: 'rgba(155, 89, 182, 0.06)',
                border: '1px solid rgba(155, 89, 182, 0.12)',
                borderRadius: 'var(--radius-md)',
                fontSize: 'var(--font-size-sm)',
                color: 'var(--color-text-secondary)',
                lineHeight: 1.7,
              }}>
                🤖 {analysis.personalityDescription}
              </div>
            )}
          </div>
        </section>

        {/* Lucky Attributes */}
        <section className="section animate-fade-in-up stagger-5">
          <h2 className="section-title">
            <span className="section-title-icon">🍀</span> 길한 속성
          </h2>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-md)' }}>
            <div className="glass-card" style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '24px', marginBottom: 'var(--space-sm)' }}>🧭</div>
              <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginBottom: '4px' }}>방위</div>
              <div style={{ fontWeight: 600 }}>{marketingVariables.luckyDirection}</div>
            </div>
            <div className="glass-card" style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '24px', marginBottom: 'var(--space-sm)' }}>🎨</div>
              <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginBottom: '4px' }}>행운의 색</div>
              <div style={{ display: 'flex', gap: '4px', justifyContent: 'center' }}>
                {marketingVariables.preferredColors.map((c, i) => (
                  <div key={i} style={{ width: 20, height: 20, borderRadius: '50%', background: c, border: '1px solid rgba(255,255,255,0.2)' }} />
                ))}
              </div>
            </div>
            <div className="glass-card" style={{ gridColumn: '1 / -1' }}>
              <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginBottom: 'var(--space-sm)' }}>🍜 추천 음식</div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px' }}>
                {marketingVariables.preferredFoods.map((f, i) => (
                  <span key={i} className="tag">{f}</span>
                ))}
              </div>
            </div>
          </div>
        </section>

        {/* CTA */}
        <section style={{ textAlign: 'center', padding: 'var(--space-xl) 0 var(--space-3xl)' }}>
          <Link href="/context" className="btn btn-primary btn-lg">
            맞춤 추천 받기 💫
          </Link>
        </section>
      </div>
    </div>
  );
}

function InfoChip({ label, value }: { label: string; value: string }) {
  return (
    <div style={{ textAlign: 'center' }}>
      <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginBottom: '2px' }}>{label}</div>
      <div style={{ fontSize: 'var(--font-size-sm)', fontWeight: 600, color: 'var(--color-gold)' }}>{value}</div>
    </div>
  );
}

function RadarChart({ elements, distribution, max }: { elements: string[]; distribution: Record<string, number>; max: number }) {
  const size = 200;
  const center = size / 2;
  const radius = 70;
  const levels = 4;

  const elementColors: Record<string, string> = {
    '목': '#4CAF50', '화': '#FF5722', '토': '#FFC107', '금': '#9E9E9E', '수': '#2196F3',
  };

  const getPoint = (index: number, value: number) => {
    const angle = (Math.PI * 2 * index) / elements.length - Math.PI / 2;
    const r = (value / max) * radius;
    return {
      x: center + r * Math.cos(angle),
      y: center + r * Math.sin(angle),
    };
  };

  const dataPoints = elements.map((el, i) => getPoint(i, distribution[el] || 0));
  const polygon = dataPoints.map(p => `${p.x},${p.y}`).join(' ');

  return (
    <div className="radar-chart-container">
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        {/* Grid levels */}
        {Array.from({ length: levels }, (_, level) => {
          const r = (radius * (level + 1)) / levels;
          const points = elements.map((_, i) => {
            const angle = (Math.PI * 2 * i) / elements.length - Math.PI / 2;
            return `${center + r * Math.cos(angle)},${center + r * Math.sin(angle)}`;
          }).join(' ');
          return <polygon key={level} points={points} fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth="1" />;
        })}

        {/* Axes */}
        {elements.map((_, i) => {
          const angle = (Math.PI * 2 * i) / elements.length - Math.PI / 2;
          return (
            <line key={i}
              x1={center} y1={center}
              x2={center + radius * Math.cos(angle)}
              y2={center + radius * Math.sin(angle)}
              stroke="rgba(255,255,255,0.06)" strokeWidth="1"
            />
          );
        })}

        {/* Data polygon */}
        <polygon points={polygon} fill="rgba(212,165,116,0.15)" stroke="var(--color-gold)" strokeWidth="2" />

        {/* Data points */}
        {dataPoints.map((p, i) => (
          <circle key={i} cx={p.x} cy={p.y} r="4" fill={elementColors[elements[i]]} stroke="#fff" strokeWidth="1" />
        ))}

        {/* Labels */}
        {elements.map((el, i) => {
          const angle = (Math.PI * 2 * i) / elements.length - Math.PI / 2;
          const labelR = radius + 20;
          return (
            <text
              key={el}
              x={center + labelR * Math.cos(angle)}
              y={center + labelR * Math.sin(angle)}
              textAnchor="middle"
              dominantBaseline="middle"
              fill={elementColors[el]}
              fontSize="12"
              fontWeight="600"
            >
              {el}
            </text>
          );
        })}
      </svg>
    </div>
  );
}
