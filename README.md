# Notion Markdown Converter

Notion으로 작성한 페이지가 Markdown으로 Export될 때 .zip 파일을 변환해주는 스크립트

<hr>

## 1. Installation & setup
### Version
> npm 패키지 설치와 동일하며, 작성된 버전은 아래와 같습니다.

|Language|Library|Version|
|:------:|-------|------:|
|JavaScript|node|14.15.4|
|-|child_process|1.0.2|
|-|dotenv|9.0.2|
|Java|java|jdk1.8.0_181|

### Command
- 설치
    ```sh
    npm install
    ```

<hr>

## 2. Usage
간단한 사용법을 기재한 항목입니다.
> **npm 설치는 필수**입니다!!

### Step 1 - Download Source
원하는 경로에 NotionToMarkdown 코드를 다운로드합니다.

### Step 2 - Convert
사용하시는 에디터로 아래 명령어를 실행합니다.
  - 파일 절대경로를 확장자까지 입력해주시면 됩니다.

```sh
npm start <<--파일경로-->>
# ex) npm start D:/sicument/TestWorkspace/Export-5ec30bba-214c-463e-b986-aa805233cbf7.zip
```

<hr>

## 3. Note
1. 현재 확장성이 용이하지 못합니다.
   - `ko-KR`만 지원합니다.
   - 변환 명령어 사용 시 모든 값을 입력해야 합니다.
   - 압축된 파일만 지원합니다.

<hr>

## License

이 저장소는 [MIT 라이센스](https://opensource.org/licenses/MIT)의 조건에 따라 오픈 소스로 제공됩니다.