'use client';

import type { WeatherData } from '@/types/saju';
import { getWeatherEmoji, getElementClass } from '@/types/saju';

interface WeatherCardProps {
  weather: WeatherData;
}

export default function WeatherCard({ weather }: WeatherCardProps) {
  return (
    <div className="weather-card">
      <div className="weather-icon">{getWeatherEmoji(weather.main)}</div>
      <div className="weather-info">
        <div className="weather-temp">{Math.round(weather.temperature)}°</div>
        <div className="weather-desc">{weather.description} · {weather.city}</div>
        <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginTop: '2px' }}>
          습도 {weather.humidity}% · 바람 {weather.windSpeed}m/s
        </div>
      </div>
      <div style={{ textAlign: 'right' }}>
        <span className={`element-badge ${getElementClass(weather.elementMapping)}`}>
          {weather.elementMapping}
        </span>
        <div style={{ fontSize: 'var(--font-size-xs)', color: 'var(--color-text-muted)', marginTop: '4px' }}>
          야외 적합 {weather.outdoorScore}점
        </div>
      </div>
    </div>
  );
}
