'use client';

import type { MoodOption } from '@/types/saju';

interface MoodSelectorProps {
  moods: MoodOption[];
  selected: string;
  onSelect: (id: string) => void;
}

export default function MoodSelector({ moods, selected, onSelect }: MoodSelectorProps) {
  return (
    <div className="mood-grid">
      {moods.map((mood) => (
        <button
          key={mood.id}
          id={`mood-${mood.id}`}
          className={`mood-item ${selected === mood.id ? 'selected' : ''}`}
          onClick={() => onSelect(mood.id)}
          aria-pressed={selected === mood.id}
          title={mood.label}
        >
          <span className="mood-emoji">{mood.emoji}</span>
          <span className="mood-label">{mood.label}</span>
        </button>
      ))}
    </div>
  );
}
