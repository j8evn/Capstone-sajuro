package com.capstone.sajurecommender.features.recommend.data;

import com.capstone.sajurecommender.features.saju.domain.SajuConstants.Element;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mock place data for the Seoul metropolitan area.
 * Each place is tagged with Five Element attributes for saju-based matching.
 */
@Component
public class PlaceData {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Place {
        private String id;
        private String name;
        private String category;       // cafe, restaurant, park, culture, shopping
        private String description;
        private double lat;
        private double lon;
        private String address;
        private Element primaryElement;
        private Element secondaryElement;
        private String atmosphere;     // cozy, lively, quiet, modern, traditional
        private int priceRange;        // 1-5
        private String imageUrl;
        private List<MenuItem> menuItems;
        private List<String> tags;
        private List<String> suitableMoods;  // happy, sad, angry, calm, thoughtful, excited
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItem {
        private String name;
        private String description;
        private int price;
        private Element element;
        private String imageUrl;
    }

    public List<Place> getAllPlaces() {
        return List.of(
            // ==================== 카페 ====================
            Place.builder()
                .id("cafe-01").name("숲속의 정원 카페").category("cafe")
                .description("도심 속 작은 숲을 연상시키는 식물 가득한 힐링 카페")
                .lat(37.5580).lon(126.9857).address("서울 종로구 삼청로 42")
                .primaryElement(Element.WOOD).secondaryElement(Element.EARTH)
                .atmosphere("quiet").priceRange(2)
                .imageUrl("/images/places/forest-cafe.jpg")
                .tags(List.of("힐링", "식물", "자연", "조용한"))
                .suitableMoods(List.of("calm", "sad", "thoughtful"))
                .menuItems(List.of(
                    MenuItem.builder().name("녹차 라떼").description("유기농 말차로 만든 부드러운 라떼").price(6500).element(Element.WOOD).build(),
                    MenuItem.builder().name("허브 티").description("캐모마일과 라벤더 블렌딩").price(5500).element(Element.WOOD).build(),
                    MenuItem.builder().name("당근 케이크").description("크림치즈 프로스팅의 수제 케이크").price(7000).element(Element.EARTH).build()
                ))
                .build(),

            Place.builder()
                .id("cafe-02").name("화로 로스터리").category("cafe")
                .description("직접 로스팅한 스페셜티 원두를 맛볼 수 있는 열정 가득한 카페")
                .lat(37.5447).lon(126.9511).address("서울 마포구 연남로 12")
                .primaryElement(Element.FIRE).secondaryElement(Element.EARTH)
                .atmosphere("lively").priceRange(3)
                .imageUrl("/images/places/fire-roastery.jpg")
                .tags(List.of("스페셜티", "로스팅", "활기찬", "인스타"))
                .suitableMoods(List.of("happy", "excited"))
                .menuItems(List.of(
                    MenuItem.builder().name("에스프레소").description("싱글 오리진 다크 로스트").price(4500).element(Element.FIRE).build(),
                    MenuItem.builder().name("아포가토").description("바닐라 젤라토에 에스프레소 샷").price(7500).element(Element.FIRE).build(),
                    MenuItem.builder().name("바스크 치즈케이크").description("진한 풍미의 시그니처 디저트").price(8000).element(Element.EARTH).build()
                ))
                .build(),

            Place.builder()
                .id("cafe-03").name("달빛 수공방").category("cafe")
                .description("금속 공예 소품이 가득한 감성적인 카페 겸 아틀리에")
                .lat(37.5635).lon(126.9830).address("서울 종로구 인사동길 28")
                .primaryElement(Element.METAL).secondaryElement(Element.WATER)
                .atmosphere("modern").priceRange(3)
                .imageUrl("/images/places/moonlight-atelier.jpg")
                .tags(List.of("감성", "공예", "모던", "인테리어"))
                .suitableMoods(List.of("calm", "thoughtful"))
                .menuItems(List.of(
                    MenuItem.builder().name("유자 에이드").description("직접 담근 유자청 에이드").price(6000).element(Element.METAL).build(),
                    MenuItem.builder().name("흑임자 라떼").description("고소한 흑임자 파우더 라떼").price(6500).element(Element.WATER).build()
                ))
                .build(),

            Place.builder()
                .id("cafe-04").name("물의 정원").category("cafe")
                .description("실내 분수와 수경 재배가 어우러진 힐링 공간")
                .lat(37.5172).lon(127.0218).address("서울 강남구 신사동 654")
                .primaryElement(Element.WATER).secondaryElement(Element.WOOD)
                .atmosphere("quiet").priceRange(4)
                .imageUrl("/images/places/water-garden.jpg")
                .tags(List.of("힐링", "물", "명상", "프리미엄"))
                .suitableMoods(List.of("sad", "calm", "thoughtful"))
                .menuItems(List.of(
                    MenuItem.builder().name("블루 레모네이드").description("버터플라이 피 컬러 레모네이드").price(8000).element(Element.WATER).build(),
                    MenuItem.builder().name("아이스 말차").description("우지 말차에 우유를 더한 시원한 음료").price(7000).element(Element.WOOD).build()
                ))
                .build(),

            // ==================== 음식점 ====================
            Place.builder()
                .id("rest-01").name("산들바람 한정식").category("restaurant")
                .description("제철 식재료로 정성껏 차린 전통 한정식")
                .lat(37.5729).lon(126.9795).address("서울 종로구 북촌로 35")
                .primaryElement(Element.EARTH).secondaryElement(Element.WOOD)
                .atmosphere("traditional").priceRange(4)
                .imageUrl("/images/places/korean-cuisine.jpg")
                .tags(List.of("한정식", "전통", "건강식", "정성"))
                .suitableMoods(List.of("calm", "happy", "thoughtful"))
                .menuItems(List.of(
                    MenuItem.builder().name("계절 한정식").description("10가지 반찬의 코스 한정식").price(35000).element(Element.EARTH).build(),
                    MenuItem.builder().name("전통 약선 죽").description("오곡과 약재를 넣은 건강 죽").price(12000).element(Element.EARTH).build()
                ))
                .build(),

            Place.builder()
                .id("rest-02").name("홍염 불닭").category("restaurant")
                .description("다양한 레벨의 매운맛을 즐길 수 있는 불닭 전문점")
                .lat(37.5560).lon(126.9234).address("서울 마포구 홍대입구 78")
                .primaryElement(Element.FIRE).secondaryElement(Element.METAL)
                .atmosphere("lively").priceRange(2)
                .imageUrl("/images/places/fire-chicken.jpg")
                .tags(List.of("매운맛", "도전", "활기", "젊은"))
                .suitableMoods(List.of("angry", "excited", "happy"))
                .menuItems(List.of(
                    MenuItem.builder().name("오리지널 불닭").description("시그니처 매운 양념 불닭").price(16000).element(Element.FIRE).build(),
                    MenuItem.builder().name("치즈 불닭").description("모짜렐라 치즈가 듬뿍 올라간 불닭").price(18000).element(Element.FIRE).build()
                ))
                .build(),

            Place.builder()
                .id("rest-03").name("동해 수산").category("restaurant")
                .description("싱싱한 회와 해산물을 즐길 수 있는 횟집")
                .lat(37.5133).lon(127.0597).address("서울 송파구 잠실로 22")
                .primaryElement(Element.WATER).secondaryElement(Element.METAL)
                .atmosphere("modern").priceRange(4)
                .imageUrl("/images/places/seafood.jpg")
                .tags(List.of("회", "해산물", "신선", "프리미엄"))
                .suitableMoods(List.of("happy", "calm", "excited"))
                .menuItems(List.of(
                    MenuItem.builder().name("모둠회 대").description("광어, 우럭, 참돔 모둠").price(65000).element(Element.WATER).build(),
                    MenuItem.builder().name("매운탕").description("싱싱한 해산물 매운탕").price(15000).element(Element.WATER).build()
                ))
                .build(),

            Place.builder()
                .id("rest-04").name("청목원").category("restaurant")
                .description("유기농 채소를 활용한 건강 비건 레스토랑")
                .lat(37.5413).lon(127.0565).address("서울 강남구 청담동 91")
                .primaryElement(Element.WOOD).secondaryElement(Element.EARTH)
                .atmosphere("quiet").priceRange(3)
                .imageUrl("/images/places/vegan-restaurant.jpg")
                .tags(List.of("비건", "유기농", "건강", "친환경"))
                .suitableMoods(List.of("calm", "thoughtful", "sad"))
                .menuItems(List.of(
                    MenuItem.builder().name("가든 샐러드 볼").description("12가지 유기농 채소 샐러드").price(16000).element(Element.WOOD).build(),
                    MenuItem.builder().name("두부 스테이크").description("수제 두부와 버섯 소스").price(18000).element(Element.WOOD).build()
                ))
                .build(),

            // ==================== 공원/자연 ====================
            Place.builder()
                .id("park-01").name("남산공원 산책로").category("park")
                .description("도심 속 자연을 만끽할 수 있는 남산 둘레길")
                .lat(37.5512).lon(126.9882).address("서울 중구 남산공원길 105")
                .primaryElement(Element.WOOD).secondaryElement(Element.FIRE)
                .atmosphere("quiet").priceRange(1)
                .imageUrl("/images/places/namsan-park.jpg")
                .tags(List.of("산책", "자연", "운동", "뷰"))
                .suitableMoods(List.of("calm", "sad", "thoughtful", "happy"))
                .menuItems(List.of(
                    MenuItem.builder().name("남산 케이블카").description("남산 정상까지 케이블카 탑승").price(11000).element(Element.FIRE).build()
                ))
                .build(),

            Place.builder()
                .id("park-02").name("한강 뚝섬공원").category("park")
                .description("한강변에서 여유로운 피크닉과 자전거를 즐기는 공간")
                .lat(37.5310).lon(127.0660).address("서울 광진구 자양동 한강변")
                .primaryElement(Element.WATER).secondaryElement(Element.WOOD)
                .atmosphere("lively").priceRange(1)
                .imageUrl("/images/places/hangang-park.jpg")
                .tags(List.of("피크닉", "자전거", "한강", "석양"))
                .suitableMoods(List.of("happy", "excited", "calm"))
                .menuItems(List.of(
                    MenuItem.builder().name("편의점 도시락 세트").description("한강 피크닉 필수 아이템").price(8000).element(Element.EARTH).build(),
                    MenuItem.builder().name("자전거 대여").description("따릉이 또는 전동 킥보드").price(3000).element(Element.METAL).build()
                ))
                .build(),

            // ==================== 문화공간 ====================
            Place.builder()
                .id("cult-01").name("국립중앙박물관").category("culture")
                .description("한국의 역사와 문화를 만나는 대한민국 대표 박물관")
                .lat(37.5238).lon(126.9806).address("서울 용산구 서빙고로 137")
                .primaryElement(Element.EARTH).secondaryElement(Element.METAL)
                .atmosphere("quiet").priceRange(1)
                .imageUrl("/images/places/national-museum.jpg")
                .tags(List.of("역사", "문화", "교육", "전시"))
                .suitableMoods(List.of("thoughtful", "calm"))
                .menuItems(List.of(
                    MenuItem.builder().name("뮤지엄 카페").description("박물관 내 카페 음료").price(5500).element(Element.EARTH).build(),
                    MenuItem.builder().name("도록/기념품").description("전시 관련 기념품").price(15000).element(Element.METAL).build()
                ))
                .build(),

            Place.builder()
                .id("cult-02").name("대림미술관").category("culture")
                .description("현대미술과 디자인 트렌드를 선도하는 미술관")
                .lat(37.5798).lon(126.9727).address("서울 종로구 자하문로 21")
                .primaryElement(Element.METAL).secondaryElement(Element.WATER)
                .atmosphere("modern").priceRange(2)
                .imageUrl("/images/places/daelim-museum.jpg")
                .tags(List.of("현대미술", "디자인", "인스타", "트렌드"))
                .suitableMoods(List.of("thoughtful", "calm", "happy"))
                .menuItems(List.of(
                    MenuItem.builder().name("전시 입장권").description("현재 전시 관람").price(8000).element(Element.METAL).build(),
                    MenuItem.builder().name("아트 굿즈").description("전시 연계 디자인 소품").price(20000).element(Element.METAL).build()
                ))
                .build(),

            // ==================== 쇼핑 ====================
            Place.builder()
                .id("shop-01").name("가로수길 편집샵 거리").category("shopping")
                .description("감각적인 편집샵과 디자이너 브랜드가 모인 트렌디한 거리")
                .lat(37.5197).lon(127.0230).address("서울 강남구 가로수길")
                .primaryElement(Element.METAL).secondaryElement(Element.FIRE)
                .atmosphere("modern").priceRange(4)
                .imageUrl("/images/places/garosugil.jpg")
                .tags(List.of("패션", "트렌드", "쇼핑", "디자이너"))
                .suitableMoods(List.of("happy", "excited"))
                .menuItems(List.of(
                    MenuItem.builder().name("시즌 컬렉션").description("신진 디자이너 브랜드 신상품").price(89000).element(Element.METAL).build(),
                    MenuItem.builder().name("향수/소품").description("니치 향수 및 라이프스타일 소품").price(45000).element(Element.METAL).build()
                ))
                .build(),

            Place.builder()
                .id("shop-02").name("익선동 한옥 골목").category("shopping")
                .description("전통 한옥을 개조한 아기자기한 숍과 카페가 모인 골목")
                .lat(37.5735).lon(126.9882).address("서울 종로구 익선동")
                .primaryElement(Element.EARTH).secondaryElement(Element.WOOD)
                .atmosphere("traditional").priceRange(2)
                .imageUrl("/images/places/ikseon-dong.jpg")
                .tags(List.of("한옥", "레트로", "아기자기", "전통"))
                .suitableMoods(List.of("happy", "calm", "thoughtful"))
                .menuItems(List.of(
                    MenuItem.builder().name("전통 공예품").description("한지, 도자기 등 전통 소품").price(25000).element(Element.EARTH).build(),
                    MenuItem.builder().name("한복 체험").description("생활 한복 입어보기 체험").price(30000).element(Element.EARTH).build()
                ))
                .build(),

            // ==================== 추가 장소 ====================
            Place.builder()
                .id("cafe-05").name("흙담 도예 카페").category("cafe")
                .description("도예 체험과 차 한 잔을 즐길 수 있는 특별한 공간")
                .lat(37.5665).lon(126.9780).address("서울 종로구 율곡로 56")
                .primaryElement(Element.EARTH).secondaryElement(Element.FIRE)
                .atmosphere("traditional").priceRange(3)
                .imageUrl("/images/places/pottery-cafe.jpg")
                .tags(List.of("도예", "체험", "전통차", "핸드메이드"))
                .suitableMoods(List.of("calm", "thoughtful", "sad"))
                .menuItems(List.of(
                    MenuItem.builder().name("도예 원데이 클래스").description("나만의 머그컵 만들기").price(35000).element(Element.EARTH).build(),
                    MenuItem.builder().name("보이차").description("운남산 보이차 한 잔").price(8000).element(Element.EARTH).build()
                ))
                .build(),

            Place.builder()
                .id("rest-05").name("금빛 갈비").category("restaurant")
                .description("숯불에 구운 프리미엄 한우 갈비 전문점")
                .lat(37.4979).lon(127.0276).address("서울 강남구 역삼동 123")
                .primaryElement(Element.METAL).secondaryElement(Element.FIRE)
                .atmosphere("modern").priceRange(5)
                .imageUrl("/images/places/gold-galbi.jpg")
                .tags(List.of("한우", "프리미엄", "회식", "특별한 날"))
                .suitableMoods(List.of("happy", "excited"))
                .menuItems(List.of(
                    MenuItem.builder().name("한우 갈비 세트").description("1++ 등급 한우 양념갈비").price(85000).element(Element.METAL).build(),
                    MenuItem.builder().name("냉면").description("갈비와 함께하는 물냉면").price(12000).element(Element.WATER).build()
                ))
                .build()
        );
    }
}
