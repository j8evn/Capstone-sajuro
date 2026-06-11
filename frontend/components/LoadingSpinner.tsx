'use client';

export default function LoadingSpinner({ text = '로딩 중...' }: { text?: string }) {
  return (
    <div className="loading-container">
      <div className="loading-spinner" />
      <p className="loading-text">{text}</p>
    </div>
  );
}
