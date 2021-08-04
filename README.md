# dev-client-app
if you want to see this code, check out master branch
## 프로젝트 설명
`RestTemplate` 통신으로 서버와 클라이언트를 분리 운영하였습니다.   
`Map<String, String[]> requestMap = request.getParameterMap();` 을 인자로 받는 `ConvertUriUtil` 객체를 직접 작성하여   
`Server API`에 요청 `URI`를 간단히 작성할 수 있도록 하였습니다.   
`POI`와 `Gson` 라이브러리를 사용하여 간단히 excel 다운로드를 할 수 있도록 `ExcelFile` 클래스로 모듈화 하였습니다.   
`c3` 라이브러리를 사용하여 간단한 Member 현황 Dashboard 화면을 구현하였습니다.   
`Spring Websocket`을 사용하여 Member 등록/수정시 Dashboard 화면의 데이터가 바로 반영될 수 있도록 구현하였습니다.
