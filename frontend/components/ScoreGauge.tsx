'use client';

interface ScoreGaugeProps {
  score: number;
  size?: number;
}

export default function ScoreGauge({ score, size = 120 }: ScoreGaugeProps) {
  const r = (size / 2) - 10;
  const circumference = 2 * Math.PI * r;
  const offset = circumference * (1 - score / 100);

  const strokeColor =
    score >= 70
      ? 'var(--color-emerald)'
      : score >= 40
      ? 'var(--color-gold)'
      : 'var(--color-fire)';

  return (
    <div className="score-gauge">
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        <circle
          cx={size / 2}
          cy={size / 2}
          r={r}
          className="score-gauge-bg"
        />
        <circle
          cx={size / 2}
          cy={size / 2}
          r={r}
          className="score-gauge-fill"
          style={{
            stroke: strokeColor,
            strokeDasharray: `${circumference}`,
            strokeDashoffset: `${offset}`,
          }}
        />
      </svg>
      <div className="score-gauge-text">
        <div className="score-gauge-value">{score}</div>
        <div className="score-gauge-label">/ 100</div>
      </div>
    </div>
  );
}
