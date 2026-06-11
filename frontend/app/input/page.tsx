'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { apiPost } from '@/lib/api';
import type { ApiResponse, SajuProfile, SajuRequest } from '@/types/saju';

// 간단한 실시간 미리보기용 사주 계산 (프론트엔드 추정값)
const HEAVENLY_STEMS = ['갑', '을', '병', '정', '무', '기', '경', '신', '임', '계'];
const EARTHLY_BRANCHES = ['자', '축', '인', '묘', '진', '사', '오', '미', '신', '유', '술', '해'];
const ELEMENTS = ['목', '화', '토', '금', '수'];
const STEM_ELEMENTS = ['목', '목', '화', '화', '토', '토', '금', '금', '수', '수'];
const BRANCH_ELEMENTS = ['수', '토', '목', '목', '토', '화', '화', '토', '금', '금', '토', '수'];

function previewYearPillar(year: number) {
  const offset = year - 4;
  const stem = HEAVENLY_STEMS[((offset % 10) + 10) % 10];
  const branch = EARTHLY_BRANCHES[((offset % 12) + 12) % 12];
  const stemEl = STEM_ELEMENTS[((offset % 10) + 10) % 10];
  const branchEl = BRANCH_ELEMENTS[((offset % 12) + 12) % 12];
  return { stem, branch, stemEl, branchEl };
}

function previewDayPillar(year: number, month: number, day: number) {
  // 기준일: 1900-01-01 = 甲子(0)
  const ref = new Date(1900, 0, 1);
  const target = new Date(year, month - 1, day);
  const diffDays = Math.round((target.getTime() - ref.getTime()) / 86400000);
  const stemIdx = ((diffDays % 10) + 10) % 10;
  const branchIdx = ((diffDays % 12) + 12) % 12;
  return {
    stem: HEAVENLY_STEMS[stemIdx],
    branch: EARTHLY_BRANCHES[branchIdx],
    stemEl: STEM_ELEMENTS[stemIdx],
    branchEl: BRANCH_ELEMENTS[branchIdx],
  };
}

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

interface PreviewPillar {
  stem: string;
  branch: string;
  stemEl: string;
  branchEl: string;
}

const ELEMENT_COLORS: Record<string, string> = {
  목: '#4CAF50', 화: '#FF5722', 토: '#FFC107', 금: '#9E9E9E', 수: '#2196F3',
};

function MiniPillar({ label, pillar }: { label: string; pillar: PreviewPillar | null }) {
  if (!pillar) {
    return (
      <div style={{
        textAlign: 'center',
        padding: 'var(--space-sm)',
        background: 'rgba(255,255,255,0.03)',
        borderRadius: 'var(--radius-md)',
        border: '1px dashed rgba(255,255,255,0.08)',
        minWidth: '56px',
      }}>
        <div style={{ fontSize: '10px', color: 'var(--color-text-muted)', marginBottom: '4px' }}>{label}</div>
        <div style={{ fontSize: '18px', color: 'rgba(255,255,255,0.15)' }}>?</div>
        <div style={{ fontSize: '18px', color: 'rgba(255,255,255,0.15)' }}>?</div>
      </div>
    );
  }
  return (
    <div style={{
      textAlign: 'center',
      padding: 'var(--space-sm)',
      background: 'rgba(255,255,255,0.04)',
      borderRadius: 'var(--radius-md)',
      border: '1px solid rgba(255,255,255,0.08)',
      minWidth: '56px',
      animation: 'fadeInUp 0.4s ease both',
    }}>
      <div style={{ fontSize: '10px', color: 'var(--color-text-muted)', marginBottom: '4px' }}>{label}</div>
      <div style={{ fontSize: '20px', fontWeight: 700, color: ELEMENT_COLORS[pillar.stemEl] || 'var(--color-gold)' }}>
        {pillar.stem}
      </div>
      <div style={{ fontSize: '20px', fontWeight: 700, color: ELEMENT_COLORS[pillar.branchEl] || 'var(--color-text-secondary)' }}>
        {pillar.branch}
      </div>
      <div style={{ fontSize: '9px', color: 'var(--color-text-muted)', marginTop: '2px' }}>
        {pillar.stemEl}·{pillar.branchEl}
      </div>
    </div>
  );
}

export default function InputPage() {
  const router = useRouter();
  const [calendarType, setCalendarType] = useState('solar');
  const [isLeapMonth, setIsLeapMonth] = useState(false);
  const [gender, setGender] = useState('');
  const [year, setYear] = useState('');
  const [month, setMonth] = useState('');
  const [day, setDay] = useState('');
  const [hour, setHour] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // 실시간 미리보기 상태
  const [preview, setPreview] = useState<{
    year: PreviewPillar | null;
    day: PreviewPillar | null;
  }>({ year: null, day: null });

  const isValid = year && month && day && hour !== '';

  // 입력 변경 시 실시간 미리보기 업데이트
  useEffect(() => {
    const y = parseInt(year);
    const m = parseInt(month);
    const d = parseInt(day);

    if (y >= 1900 && y <= 2100) {
      setPreview(prev => ({ ...prev, year: previewYearPillar(y) }));
    } else {
      setPreview(prev => ({ ...prev, year: null }));
    }

    if (y >= 1900 && m >= 1 && m <= 12 && d >= 1 && d <= 31) {
      try {
        const date = new Date(y, m - 1, d);
        if (!isNaN(date.getTime()) && date.getDate() === d) {
          setPreview(prev => ({ ...prev, day: previewDayPillar(y, m, d) }));
        } else {
          setPreview(prev => ({ ...prev, day: null }));
        }
      } catch {
        setPreview(prev => ({ ...prev, day: null }));
      }
    } else {
      setPreview(prev => ({ ...prev, day: null }));
    }
  }, [year, month, day]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!isValid) return;

    setLoading(true);
    setError('');

    try {
      const request: SajuRequest & { leapMonth?: boolean } = {
        year: parseInt(year),
        month: parseInt(month),
        day: parseInt(day),
        hour: parseInt(hour),
        calendarType,
        leapMonth: calendarType === 'lunar' ? isLeapMonth : false,
        gender: gender || 'unknown',
      };

      const result = await apiPost<ApiResponse<SajuProfile>>('/api/saju/calculate', request);

      if (result.success) {
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

        {/* 실시간 사주 미리보기 */}
        <section className="section animate-fade-in-up stagger-2">
          <h2 className="section-title">
            <span className="section-title-icon">👁️</span> 실시간 미리보기
          </h2>
          <div className="glass-card">
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 'var(--space-sm)' }}>
              <MiniPillar label="년주(年)" pillar={preview.year} />
              <MiniPillar label="월주(月)" pillar={null} />
              <MiniPillar label="일주(日)" pillar={preview.day} />
              <MiniPillar label="시주(時)" pillar={null} />
            </div>
            <p style={{
              fontSize: 'var(--font-size-xs)',
              color: 'var(--color-text-muted)',
              textAlign: 'center',
              marginTop: 'var(--space-sm)',
            }}>
              년주·일주는 입력 즉시 미리보기 (월주·시주는 절기 계산 필요)
            </p>
          </div>
        </section>

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
            {/* 윤달 토글 (음력 선택 시만 표시) */}
            {calendarType === 'lunar' && (
              <div style={{ marginTop: 'var(--space-sm)' }}>
                <button
                  type="button"
                  className={`toggle-option ${isLeapMonth ? 'active' : ''}`}
                  style={{ width: 'auto', padding: '6px 16px', fontSize: 'var(--font-size-sm)' }}
                  onClick={() => setIsLeapMonth(v => !v)}
                >
                  {isLeapMonth ? '✅ 윤달' : '⬜ 윤달'}
                </button>
              </div>
            )}
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
                  id="birth-year"
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
                  id="birth-month"
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
                  id="birth-day"
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
              id="birth-hour"
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
              id="submit-saju"
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
