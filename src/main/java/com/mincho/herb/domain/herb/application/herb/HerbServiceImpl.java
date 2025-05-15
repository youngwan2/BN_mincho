package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.embedding.dto.RecommendHerbsDTO;
import com.mincho.herb.global.aop.UserActivityAction;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.dto.PageInfoDTO;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.MapperUtils;
import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.domain.HerbViews;
import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.HerbViewsEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.herb.repository.herbViews.HerbViewsRepository;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HerbServiceImpl implements HerbService{
    private final HerbRepository herbRepository;
    private final UserRepository userRepository;
    private final HerbViewsRepository herbViewsRepository;
    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final MapperUtils mapperUtils;


    // 약초 등록
    @Override
    @Transactional
    public void createHerb(HerbCreateRequestDTO herbCreateRequestDTO) {
       String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       if(roles.equalsIgnoreCase("ROLE_ADMIN")){
           throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
       }

       String email = (String) SecurityContextHolder.getContext().getAuthentication().getName();

       if(!email.contains("@")) {
           throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "로그인 후 이용 가능합니다.");
       }

       Long adminId = userRepository.findByEmail(email).getId();

        HerbEntity unsavedHerbEntity = HerbEntity.builder()
                .cntntsSj(herbCreateRequestDTO.getCntntsSj())
                .hbdcNm(herbCreateRequestDTO.getHbdcNm())
                .useeRegn(herbCreateRequestDTO.getUseeRegn())
                .prvateTherpy(herbCreateRequestDTO.getPrvateTherpy())
                .bneNm(herbCreateRequestDTO.getBneNm())
                .growthForm(herbCreateRequestDTO.getGrowthForm())
                .flowering(herbCreateRequestDTO.getFlowering())
                .habitat(herbCreateRequestDTO.getHabitat())
                .harvest(herbCreateRequestDTO.getHarvest())
                .adminId(adminId)
                .build();

        herbRepository.save(unsavedHerbEntity);
    }

    // 약초명으로 약초 찾기
    @Override
    public Herb getHerbByHerbName(String herbName) {
        HerbEntity herbEntity =  herbRepository.findByCntntsSj(herbName);
        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "조회 데이터가 없습니다.");
        }
        return herbEntity.toModel();
    }


    // 약초 목록 조회(페이징)
    @Override
    public List<HerbDTO> getHerbs(PageInfoDTO pageInfoDTO, HerbFilteringRequestDTO herbFilteringRequestDTO, HerbSort herbSort) {

        List<HerbDTO> herbs = herbRepository.findByFiltering(herbFilteringRequestDTO, herbSort, pageInfoDTO);
        if(herbs.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"조회 데이터가 없습니다.");
        }

        return herbs;

    }


    // 상세 페이지
    @Override
    @UserActivityAction(action = "herb_detail")
    public HerbDetailResponseDTO getHerbDetails(Long id) {

         HerbEntity herbEntity = herbRepository.findById(id); // 조회 약초 엔티티
         HerbViewsEntity herbViewsEntity = herbViewsRepository.findByHerb(herbEntity); // 이전 조회수 엔티티

        // 허브 조회수가 null 이면 새로 만들어서 초기화 해줌
        if(herbViewsEntity ==null){
            HerbViewsEntity newHerbViews = new HerbViewsEntity();
            newHerbViews.setViewCount(0L);
            newHerbViews.setHerb(herbEntity);
            herbViewsEntity = herbViewsRepository.save(newHerbViews);
        }

        // 이미지
         List<String> images = List.of(
                 herbEntity.getImgUrl1(),
                 herbEntity.getImgUrl2(),
                 herbEntity.getImgUrl3(),
                 herbEntity.getImgUrl4(),
                 herbEntity.getImgUrl5(),
                 herbEntity.getImgUrl6()
         );

        HerbViews herbViews = herbViewsEntity.toModel();
        Long updatedViewCount =herbViews.increase(herbViewsEntity.getViewCount()); // 조회수 증가

        log.info("herbViews:{}",herbViews);

        herbViewsRepository.save(HerbViewsEntity.toEntity(herbViews, herbEntity));


        return HerbDetailResponseDTO.builder()
                .id(herbEntity.getId())
                .bneNm(herbEntity.getBneNm())
                .cntntsNo(herbEntity.getCntntsNo())
                .cntntsSj(herbEntity.getCntntsSj())
                .prvateTherpy(herbEntity.getPrvateTherpy())
                .hbdcNm(herbEntity.getHbdcNm())
                .imgUrls(images)
                .viewCount(updatedViewCount)
                .useeRegn(herbEntity.getUseeRegn())
                .growthForm(herbEntity.getGrowthForm())
                .flowering(herbEntity.getFlowering())
                .habitat(herbEntity.getHabitat())
                .harvest(herbEntity.getHarvest())
                .createdAt(herbEntity.getCreatedAt())
                .updatedAt(herbEntity.getUpdatedAt())
                .build();

    }

    // 약초 제거
    @Override
    @Transactional
    public void removeHerb(Long id) {
        String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(roles.equalsIgnoreCase("ROLE_ADMIN")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
        }

        HerbEntity herbEntity = herbRepository.findById(id);
        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 정보는 존재하지 않습니다.");

        }

        log.info("roles: {}", roles);
        herbRepository.deleteById(id);
    }

    // 허브 수정
    @Override
    @Transactional
    public void updateHerb(HerbUpdateRequestDTO herbUpdateRequestDTO, Long herbId) {
        String roles = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(roles.equalsIgnoreCase("ROLE_ADMIN")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "관리자만 접근할 수 있습니다.");
        }

        String email = (String) SecurityContextHolder.getContext().getAuthentication().getName();

        Long adminId = userRepository.findByEmail(email).getId();

        HerbEntity herbEntity = herbRepository.findById(herbId);

        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 정보는 존재하지 않습니다.");
        }

        HerbEntity unsavedHerbEntity =  HerbEntity.builder()
                .id(herbId)
                .cntntsSj(herbUpdateRequestDTO.getCntntsSj())
                .imgUrl1(herbUpdateRequestDTO.getImgUrl1())
                .hbdcNm(herbUpdateRequestDTO.getHbdcNm())
                .bneNm(herbUpdateRequestDTO.getBneNm())
                .build();

        herbRepository.save(unsavedHerbEntity);
    }

    // 값 초기화
    @Override
    public void insertMany() throws IOException {
        List<Herb> herbs = mapperUtils.jsonMapper("herb.json", Herb.class);
        List<HerbEntity> herbsEntity = herbs.stream().map(herb -> {
            herb.setAdminId(-999L);
            return HerbEntity.toEntity(herb);
        }).toList();
        herbRepository.saveAll(herbsEntity);

    }

    // 랜덤 약초
    @Override
    public List<HerbDTO> getRandomHerbs(Long herbId) {

        // 현재 조회중인 허브를 제외한 모든 허브 id 목록 조회
        List<Long> herbIds = herbRepository.findHerbIds()
                .stream()
                .filter(id -> !Objects.equals(id, herbId))
                .toList();

        // 랜덤하게 3개 뽑기
        List<Integer> randomIds = new ArrayList<>();

        // 뽑았는데 중복이라면? 중복된 값이 안 나올 때 까지 반복 돌려야 하는거 아님?
        while(randomIds.size() <3){
                int random0to3 = (int) (Math.random() * herbIds.size()); // 랜덤하게 한 개 뽑고.
                randomIds.add(random0to3);

                // 뽑은 값이 기존 리스트에 있는감? 중복이 아니면 추가
                if(!randomIds.contains(random0to3)){
                    int random = (int) (Math.random() * herbIds.size()); // 다시 뽑아서
                    randomIds.add(random);
                }
        }


        int index1 = randomIds.get(0);
        int index2 = randomIds.get(1);
        int index3 = randomIds.get(2);

         List<HerbEntity> herbEntities = herbRepository.findRandom(herbIds.get(index1), herbIds.get(index2), herbIds.get(index3));

         return herbEntities.stream().map((herbEntity)->{
             return HerbDTO.builder()
                     .id(herbEntity.getId())
                     .bneNm(herbEntity.getBneNm())
                     .imgUrl1(herbEntity.getImgUrl1())
                     .cntntsSj(herbEntity.getCntntsSj())
                     .hbdcNm(herbEntity.getHbdcNm())
                     .build();

         }).toList();
    }

    // 이달의 개화 약초
    @Override
    public List<HerbDTO> getHerbsBloomingThisMonth(String month) {

        List<HerbEntity> herbEntities  = herbRepository.findByMonth(month);
        return herbEntities.stream().map((herbEntity)->{
            return HerbDTO.builder()
                    .id(herbEntity.getId())
                    .bneNm(herbEntity.getBneNm())
                    .imgUrl1(herbEntity.getImgUrl1())
                    .cntntsSj(herbEntity.getCntntsSj())
                    .hbdcNm(herbEntity.getHbdcNm())
                    .build();

        }).toList();

    }


    // 약초 전체 개수
    @Override
    public Long getHerbCount(HerbFilteringRequestDTO herbFilteringRequestDTO) {
        return herbRepository.countByFiltering(herbFilteringRequestDTO);
    }

    // 실시간 인기 순위 약초
    @Override
    public List<PopularityHerbsDTO> getHerbsMostview() {
        return herbRepository.findAllByOrderByViewCountDesc();
    }

    // 유사도 기반 약초 검색
    @Override
    @UserActivityAction(action="search")
    public List<HerbDTO> getRecommendHerbs() {

        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()

                .build());

        return List.of();
    }


    // RAG 기반 유사도 검색
    @Override
    @UserActivityAction(action="ai_recommendation")
    public List<RecommendHerbsDTO> getSimilaritySearchByRag(String question) {

        List<Document> results = vectorStore.similaritySearch(SearchRequest.builder()
                .query(question)
                .topK(15)
                .build()
        );

        if(results ==null || results.isEmpty()){
            return null;
        }

        // 리스트 형태의 포맷 변환
        BeanOutputConverter<List<RecommendHerbsDTO>> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<RecommendHerbsDTO>>() {});

        // 유사도 검색 결과
        String context = results.stream().map(document -> {
            return document.getText().concat(" id: "+document.getMetadata().get("id").toString()+"\n") ;
        }).collect(Collectors.joining());

        // 포맷
        String format = converter.getFormat();

        // 프롬프트 템플릿
        String template = """
                당신은 유능한 약초 전문가입니다. 문맥에 따라 고객의 질문에 정중하게 대답해 주십시오.
                컨텍스트에 나와 있는 약초 중에서 사용자의 {question}에 등장하는 문장을 의미 기반으로 분석하고, {context} 에 등장하는 약초 목록에서 존재하는 약초를 최대 10개 내로 선택하여 포맷형태의 배열로 응답해야 합니다.
                answer 에는 왜 해당 약초를 추천하는지 명시합니다.
                만일 약초 목록에서 찾지 못했더라도, 증상과 관련해서 추가로 알아두면 도움이 되는 약초가 있으면 추천해야 합니다. 이 때 왜 추천하는지 이유도 answer에 명시해야 합니다.
                컨텍스트가 없는 경우 '현재 증상에 추천드릴 약초가 없네요. 죄송합니다.'로 시작하여, 추천하는 검색어를 answer 에 입력 후 대답하세요.
                컨텍스트가 없는 경우 id 는 -999 로 입력하여 대답하세요.
                컨텍스트가 없는 경우 herbName 은 비어두세요.
                priority 에는 우선순위로 현재 사용자에게 꼭 필요한 약초부터 1 부터 순위를 매겨주세요.
                url 은 경로 입니다. 기존적으로 '/herbs/:id' 형태입니다. 예를 들어 /herbs/148 과 같이 조회된 id 값을 입력합니다.
                
                응답은 반드시 아래 JSON 배열 형식을 따릅니다:
                {format}

                컨텍스트:
                {context}

                질문:
                {question}

                답변:
                """;

        // 프롬프트 생성
        Prompt prompt = new PromptTemplate(template, Map.of(
                "context", context,
                "question", question,
                "format", format
        )).create();

        // AI 호출
        Generation generation = chatModel.call(prompt).getResult();

        String response = generation.getOutput().getText();

        // 결과를 지정한 포맷으로 변경 후 반환
        return converter.convert(response);
    }
}
