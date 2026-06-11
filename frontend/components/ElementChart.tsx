'use client';

interface ElementChartProps {
  elements: string[];
  distribution: Record<string, number>;
  max?: number;
}

const ELEMENT_COLORS: Record<string, string> = {
  '목': '#4CAF50',
  '화': '#FF5722',
  '토': '#FFC107',
  '금': '#9E9E9E',
  '수': '#2196F3',
};

export default function ElementChart({ elements, distribution, max }: ElementChartProps) {
  const computedMax = max ?? Math.max(...elements.map(e => distribution[e] || 0), 1);

  const size = 200;
  const center = size / 2;
  const radius = 70;
  const levels = 4;

  const getPoint = (index: number, value: number) => {
    const angle = (Math.PI * 2 * index) / elements.length - Math.PI / 2;
    const r = (value / computedMax) * radius;
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
          return (
            <polygon
              key={level}
              points={points}
              fill="none"
              stroke="rgba(255,255,255,0.06)"
              strokeWidth="1"
            />
          );
        })}

        {/* Axes */}
        {elements.map((_, i) => {
          const angle = (Math.PI * 2 * i) / elements.length - Math.PI / 2;
          return (
            <line
              key={i}
              x1={center} y1={center}
              x2={center + radius * Math.cos(angle)}
              y2={center + radius * Math.sin(angle)}
              stroke="rgba(255,255,255,0.06)"
              strokeWidth="1"
            />
          );
        })}

        {/* Data polygon */}
        <polygon
          points={polygon}
          fill="rgba(212,165,116,0.15)"
          stroke="var(--color-gold)"
          strokeWidth="2"
        />

        {/* Data points */}
        {dataPoints.map((p, i) => (
          <circle
            key={i}
            cx={p.x}
            cy={p.y}
            r="4"
            fill={ELEMENT_COLORS[elements[i]]}
            stroke="#fff"
            strokeWidth="1"
          />
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
              fill={ELEMENT_COLORS[el]}
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
