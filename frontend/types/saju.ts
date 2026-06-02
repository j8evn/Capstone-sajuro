// ==================== Saju Types ====================

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface SajuRequest {
  year: number;
  month: number;
  day: number;
  hour: number;
  calendarType: string;
  gender: string;
}

export interface PillarDetail {
  stemKorean: string;
  stemHanja: string;
  branchKorean: string;
  branchHanja: string;
  stemElement: string;
  branchElement: string;
  stemElementColor: string;
  branchElementColor: string;
  yinYang: string;
  animal: string;
  display: string;
}

export interface SajuProfile {
  birthInfo: {
    year: number;
    month: number;
    day: number;
    hour: number;
    calendarType: string;
    gender: string;
  };
  fourPillars: {
    year: PillarDetail;
    month: PillarDetail;
    day: PillarDetail;
    hour: PillarDetail;
  };
  analysis: {
    dayMaster: string;
    dayMasterElement: string;
    dayMasterElementColor: string;
    elementDistribution: Record<string, number>;
    yinYangBalance: Record<string, number>;
    neededElement: string;
    neededElementColor: string;
    personalityKeywords: string[];
    socialTendency: string;
    decisionStyle: string;
    personalityDescription?: string; // AI-generated
  };
  marketingVariables: {
    preferredColors: string[];
    preferredFoods: string[];
    preferredActivities: string[];
    luckyDirection: string;
  };
  dailyFortune: {
    score: number;
    description: string;
    aiDescription?: string; // AI-generated detailed fortune
    date: string;
  };
  aiEnabled?: boolean;
}

// ==================== Recommendation Types ====================

export interface RecommendRequest {
  sajuInput: {
    year: number;
    month: number;
    day: number;
    hour: number;
    calendarType: string;
    gender: string;
  };
  mood: string;
  lat: number;
  lon: number;
  category?: string;
}

export interface MenuItemInfo {
  name: string;
  description: string;
  price: number;
  element: string;
  elementColor: string;
}

export interface ScoreBreakdown {
  sajuScore: number;
  weatherScore: number;
  moodScore: number;
  timeScore: number;
  congestionScore: number;
}

export interface PlaceRecommendation {
  id: string;
  name: string;
  category: string;
  categoryEmoji: string;
  description: string;
  address: string;
  lat: number;
  lon: number;
  atmosphere: string;
  priceRange: number;
  imageUrl: string;
  matchScore: number;
  scoreBreakdown: ScoreBreakdown;
  reasonText: string;
  matchedTags: string[];
  menuItems: MenuItemInfo[];
}

export interface RecommendResponse {
  mood: string;
  moodEmoji: string;
  weather: {
    main: string;
    description: string;
    temperature: number;
    elementMapping: string;
    elementColor: string;
  };
  recommendations: PlaceRecommendation[];
  contextSummary: {
    neededElement: string;
    neededElementColor: string;
    weatherElement: string;
    timeOfDay: string;
    fortuneScore: number;
  };
}

// ==================== Weather Types ====================

export interface WeatherData {
  city: string;
  temperature: number;
  feelsLike: number;
  humidity: number;
  windSpeed: number;
  main: string;
  description: string;
  icon: string;
  elementMapping: string;
  elementColor: string;
  outdoorScore: number;
}

// ==================== Mood Types ====================

export interface MoodOption {
  id: string;
  emoji: string;
  label: string;
}

export const MOODS: MoodOption[] = [
  { id: 'happy', emoji: '😊', label: '행복' },
  { id: 'sad', emoji: '😔', label: '우울' },
  { id: 'angry', emoji: '😤', label: '짜증' },
  { id: 'calm', emoji: '😌', label: '평온' },
  { id: 'thoughtful', emoji: '🤔', label: '고민' },
  { id: 'excited', emoji: '🎉', label: '신남' },
];

// ==================== Element Helpers ====================

export function getElementClass(elementKorean: string): string {
  const map: Record<string, string> = {
    '목': 'element-wood',
    '화': 'element-fire',
    '토': 'element-earth',
    '금': 'element-metal',
    '수': 'element-water',
  };
  return map[elementKorean] || '';
}

export function getWeatherEmoji(main: string): string {
  const map: Record<string, string> = {
    'Clear': '☀️',
    'Clouds': '☁️',
    'Rain': '🌧️',
    'Drizzle': '🌦️',
    'Thunderstorm': '⛈️',
    'Snow': '❄️',
    'Mist': '🌫️',
    'Fog': '🌫️',
  };
  return map[main] || '🌤️';
}

export function getCategoryEmoji(category: string): string {
  const map: Record<string, string> = {
    'cafe': '☕',
    'restaurant': '🍽️',
    'park': '🌿',
    'culture': '🎨',
    'shopping': '🛍️',
  };
  return map[category] || '📍';
}
