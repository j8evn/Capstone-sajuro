import type { Metadata, Viewport } from "next";
import "./globals.css";
import Navigation from "@/components/Navigation";

export const metadata: Metadata = {
  title: "사주로 - 나만의 운명 가이드",
  description: "사주 데이터 기반 초개인화 상황 인지 추천 시스템. 당신의 사주와 현재 상황을 분석하여 최적의 장소와 아이템을 추천합니다.",
  keywords: "사주, 운세, 맞춤 추천, 장소 추천, 오행, 사주팔자",
};

export const viewport: Viewport = {
  themeColor: "#0a0a1a",
  width: "device-width",
  initialScale: 1,
  maximumScale: 1,
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body>
        <div className="stars-bg" aria-hidden="true">
          {Array.from({ length: 50 }, (_, i) => (
            <div
              key={i}
              className="star"
              style={{
                left: `${Math.random() * 100}%`,
                top: `${Math.random() * 100}%`,
                '--duration': `${2 + Math.random() * 4}s`,
                '--delay': `${Math.random() * 3}s`,
              } as React.CSSProperties}
            />
          ))}
        </div>
        <main style={{ position: "relative", zIndex: 1 }}>{children}</main>
        <Navigation />
      </body>
    </html>
  );
}
