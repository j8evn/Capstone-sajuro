'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiGet, apiPost } from '@/lib/api';
import type { ApiResponse, WeatherData, RecommendResponse, SajuRequest } from '@/types/saju';
import { MOODS } from '@/types/saju';
import MoodSelector from '@/components/MoodSelector';
import WeatherCard from '@/components/WeatherCard';
import LoadingSpinner from '@/components/LoadingSpinner';

export default function ContextPage() {
  const router = useRouter();
  const [selectedMood, setSelectedMood] = useState('');
  const [weather, setWeather] = useState<WeatherData | null>(null);
  const [loading, setLoading] = useState(false);
  const [loadingWeather, setLoadingWeather] = useState(true);
  const [error, setError] = useState('');
  const [location, setLocation] = useState({ lat: 37.5665, lon: 126.978 });

  useEffect(() => {
    const stored = sessionStorage.getItem('sajuRequest');
    if (!stored) {
      router.push('/input');
      return;
    }

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setLocation({ lat: pos.coords.latitude, lon: pos.coords.longitude });
          fetchWeather(pos.coords.latitude, pos.coords.longitude);
        },
        () => fetchWeather(37.5665, 126.978),
        { timeout: 5000 }
      );
    } else {
      fetchWeather(37.5665, 126.978);
    }
  }, [router]);

  async function fetchWeather(lat: number, lon: number) {
    try {
      const res = await apiGet<ApiResponse<WeatherData>>('/api/weather', {
        lat: lat.toString(),
        lon: lon.toString(),
      });
      if (res.success) {
        setWeather(res.data);
      }
    } catch {
      setWeather({
        city: '서울',
        temperature: 22.5,
        feelsLike: 21.0,
        humidity: 55,
        windSpeed: 2.1,
        main: 'Clear',
        description: '맑음',
        icon: '01d',
        elementMapping: '화',
        elementColor: '#FF5722',
        outdoorScore: 85,
      });
    } finally {
      setLoadingWeather(false);
    }
  }

  async function handleRecommend() {
    if (!selectedMood) return;

    setLoading(true);
    setError('');

    try {
      const sajuReqStr = sessionStorage.getItem('sajuRequest');
      if (!sajuReqStr) {
        router.push('/input');
        return;
      }
      const sajuReq: SajuRequest = JSON.parse(sajuReqStr);

      const result = await apiPost<ApiResponse<RecommendResponse>>('/api/recommend', {
        sajuInput: sajuReq,
        mood: selectedMood,
        lat: location.lat,
        lon: location.lon,
      });

      if (result.success) {
        sessionStorage.setItem('recommendations', JSON.stringify(result.data));
        router.push('/recommendations');
      } else {
        setError(result.message || '추천 생성에 실패했습니다.');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '서버 연결에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page">
      <div className="container">
        <div className="page-header">
          <h1 className="page-title animate-fade-in-up stagger-1">기분 &amp; 상황</h1>
          <p className="page-subtitle animate-fade-in-up stagger-2">
            지금의 기분과 상황을 알려주세요
          </p>
        </div>

        {/* Current Weather */}
        <section className="section animate-fade-in-up stagger-2">
          <h2 className="section-title">
            <span className="section-title-icon">🌤️</span> 현재 날씨
          </h2>
          {loadingWeather ? (
            <div className="glass-card" style={{ textAlign: 'center', padding: 'var(--space-xl)' }}>
              <p className="loading-text">날씨 정보를 불러오는 중...</p>
            </div>
          ) : weather ? (
            <WeatherCard weather={weather} />
          ) : null}
        </section>

        {/* Mood Selection */}
        <section className="section animate-fade-in-up stagger-3">
          <h2 className="section-title">
            <span className="section-title-icon">💭</span> 지금 기분은?
          </h2>
          <MoodSelector
            moods={MOODS}
            selected={selectedMood}
            onSelect={setSelectedMood}
          />
        </section>

        {/* Error */}
        {error && (
          <div style={{
            padding: 'var(--space-md)',
            background: 'rgba(255, 87, 34, 0.1)',
            border: '1px solid rgba(255, 87, 34, 0.3)',
            borderRadius: 'var(--radius-md)',
            color: '#FF8A65',
            fontSize: 'var(--font-size-sm)',
            marginBottom: 'var(--space-lg)',
          }}>
            {error}
          </div>
        )}

        {/* CTA */}
        <section style={{ textAlign: 'center', padding: 'var(--space-xl) 0 var(--space-3xl)' }}>
          <button
            id="btn-find-places"
            className="btn btn-primary btn-lg"
            disabled={!selectedMood || loading}
            onClick={handleRecommend}
          >
            {loading ? (
              <>
                <span style={{ animation: 'spin 1s linear infinite', display: 'inline-block' }}>☯</span>
                추천 생성 중...
              </>
            ) : (
              '나에게 맞는 장소 찾기 🔍'
            )}
          </button>
        </section>
      </div>
    </div>
  );
}
