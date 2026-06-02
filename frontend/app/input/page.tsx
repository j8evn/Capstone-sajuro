'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { apiPost } from '@/lib/api';
import type { ApiResponse, SajuProfile, SajuRequest } from '@/types/saju';

const hours = Array.from({ length: 24 }, (_, i) => ({
  value: i,
  label: `${String(i).padStart(2, '0')}시 (${getTimePeriod(i)})`,
}));

function getTimePeriod(h: number): string {
  if (h >= 23 || h < 1) return '자시';
  if (h < 3) return '축시';
  if (h < 5) return '인시';
  if (h < 7) return '묘시';
  if (h < 9) return '진시';
  if (h < 11) return '사시';
  if (h < 13) return '오시';
  if (h < 15) return '미시';
  if (h < 17) return '신시';
  if (h < 19) return '유시';
  if (h < 21) return '술시';
  return '해시';
}

export default function InputPage() {
  const router = useRouter();
  const [calendarType, setCalendarType] = useState('solar');
  const [gender, setGender] = useState('');
  const [year, setYear] = useState('');
  const [month, setMonth] = useState('');
  const [day, setDay] = useState('');
  const [hour, setHour] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const isValid = year && month && day && hour !== '';

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!isValid) return;

    setLoading(true);
    setError('');

    try {
      const request: SajuRequest = {
        year: parseInt(year),
        month: parseInt(month),
        day: parseInt(day),
        hour: parseInt(hour),
        calendarType,
        gender: gender || 'unknown',
      };

      const result = await apiPost<ApiResponse<SajuProfile>>('/api/saju/calculate', request);
      
      if (result.success) {
        // Store in sessionStorage for next page
        sessionStorage.setItem('sajuProfile', JSON.stringify(result.data));
        sessionStorage.setItem('sajuRequest', JSON.stringify(request));
        router.push('/result');
      } else {
        setError(result.message || '계산 중 오류가 발생했습니다.');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '서버 연결에 실패했습니다. 백엔드가 실행 중인지 확인해주세요.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page">
      <div className="container">
        <div className="page-header">
          <h1 className="page-title animate-fade-in-up stagger-1">사주 입력</h1>
          <p className="page-subtitle animate-fade-in-up stagger-2">
            생년월일시를 입력하면 사주팔자를 자동으로 계산합니다
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          {/* Calendar Type Toggle */}
          <div className="form-group animate-fade-in-up stagger-2">
            <label className="form-label">달력 유형</label>
            <div className="toggle-group">
              <button
                type="button"
                className={`toggle-option ${calendarType === 'solar' ? 'active' : ''}`}
                onClick={() => setCalendarType('solar')}
              >
                양력 ☀️
              </button>
              <button
                type="button"
                className={`toggle-option ${calendarType === 'lunar' ? 'active' : ''}`}
                onClick={() => setCalendarType('lunar')}
              >
                음력 🌙
              </button>
            </div>
          </div>

          {/* Gender */}
          <div className="form-group animate-fade-in-up stagger-2">
            <label className="form-label">성별 (선택)</label>
            <div className="toggle-group">
              <button
                type="button"
                className={`toggle-option ${gender === 'male' ? 'active' : ''}`}
                onClick={() => setGender('male')}
              >
                남성 ♂️
              </button>
              <button
                type="button"
                className={`toggle-option ${gender === 'female' ? 'active' : ''}`}
                onClick={() => setGender('female')}
              >
                여성 ♀️
              </button>
            </div>
          </div>

          {/* Birth Date */}
          <div className="form-group animate-fade-in-up stagger-3">
            <label className="form-label">생년월일</label>
            <div className="form-row-3">
              <div>
                <input
                  type="number"
                  className="form-input"
                  placeholder="년도"
                  min="1900"
                  max="2100"
                  value={year}
                  onChange={(e) => setYear(e.target.value)}
                />
              </div>
              <div>
                <select
                  className="form-select"
                  value={month}
                  onChange={(e) => setMonth(e.target.value)}
                >
                  <option value="">월</option>
                  {Array.from({ length: 12 }, (_, i) => (
                    <option key={i + 1} value={i + 1}>{i + 1}월</option>
                  ))}
                </select>
              </div>
              <div>
                <select
                  className="form-select"
                  value={day}
                  onChange={(e) => setDay(e.target.value)}
                >
                  <option value="">일</option>
                  {Array.from({ length: 31 }, (_, i) => (
                    <option key={i + 1} value={i + 1}>{i + 1}일</option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Birth Time */}
          <div className="form-group animate-fade-in-up stagger-4">
            <label className="form-label">태어난 시간</label>
            <select
              className="form-select"
              value={hour}
              onChange={(e) => setHour(e.target.value)}
            >
              <option value="">시간을 선택하세요</option>
              {hours.map((h) => (
                <option key={h.value} value={h.value}>{h.label}</option>
              ))}
            </select>
          </div>

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

          {/* Submit */}
          <div className="animate-fade-in-up stagger-5">
            <button
              type="submit"
              className="btn btn-primary btn-lg"
              disabled={!isValid || loading}
            >
              {loading ? (
                <>
                  <span style={{ animation: 'spin 1s linear infinite', display: 'inline-block' }}>☯</span>
                  사주 분석 중...
                </>
              ) : (
                '사주 분석하기 ✨'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
