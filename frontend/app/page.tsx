'use client';

import Link from 'next/link';

export default function HomePage() {
  return (
    <div className="page">
      <div className="container">
        {/* Hero Section */}
        <section style={{ 
          paddingTop: '15vh', 
          textAlign: 'center',
          marginBottom: 'var(--space-3xl)',
        }}>
          <div className="animate-fade-in-up stagger-1" style={{ fontSize: '64px', marginBottom: 'var(--space-md)' }}>
            🔮
          </div>
          <h1 className="animate-fade-in-up stagger-2" style={{
            fontSize: 'var(--font-size-4xl)',
            fontWeight: 900,
            background: 'var(--gradient-gold)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
            backgroundClip: 'text',
            marginBottom: 'var(--space-md)',
            lineHeight: 1.2,
          }}>
            사주로
          </h1>
          <p className="animate-fade-in-up stagger-3" style={{
            fontSize: 'var(--font-size-lg)',
            color: 'var(--color-text-secondary)',
            marginBottom: 'var(--space-sm)',
            fontWeight: 300,
          }}>
            나만의 운명 가이드
          </p>
          <p className="animate-fade-in-up stagger-4" style={{
            fontSize: 'var(--font-size-sm)',
            color: 'var(--color-text-muted)',
            maxWidth: '300px',
            margin: '0 auto',
            lineHeight: 1.6,
          }}>
            당신의 사주와 현재 상황을 분석하여<br />
            최적의 장소와 아이템을 추천합니다
          </p>
        </section>

        {/* Features */}
        <section className="animate-fade-in-up stagger-4" style={{ marginBottom: 'var(--space-3xl)' }}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-md)' }}>
            <FeatureCard
              icon="📊"
              title="사주 데이터 표준화"
              desc="생년월일시를 입력하면 사주팔자를 자동 계산하고 오행 분석을 제공합니다"
            />
            <FeatureCard
              icon="🌦️"
              title="실시간 상황 인지"
              desc="현재 날씨, 시간대, 기분을 종합하여 최적의 추천을 생성합니다"
            />
            <FeatureCard
              icon="✨"
              title="맞춤 장소 추천"
              desc="사주 분석 결과를 기반으로 당신에게 딱 맞는 장소와 메뉴를 추천합니다"
            />
          </div>
        </section>

        {/* CTA */}
        <section className="animate-fade-in-up stagger-5" style={{ textAlign: 'center', paddingBottom: 'var(--space-3xl)' }}>
          <Link href="/input" className="btn btn-primary btn-lg">
            나의 사주 알아보기 →
          </Link>
        </section>
      </div>
    </div>
  );
}

function FeatureCard({ icon, title, desc }: { icon: string; title: string; desc: string }) {
  return (
    <div className="glass-card" style={{ display: 'flex', gap: 'var(--space-md)', alignItems: 'flex-start' }}>
      <span style={{ fontSize: '28px', flexShrink: 0 }}>{icon}</span>
      <div>
        <h3 style={{ fontSize: 'var(--font-size-md)', fontWeight: 600, marginBottom: 'var(--space-xs)' }}>
          {title}
        </h3>
        <p style={{ fontSize: 'var(--font-size-sm)', color: 'var(--color-text-secondary)', lineHeight: 1.5 }}>
          {desc}
        </p>
      </div>
    </div>
  );
}
