# Project Structure Details

Tai lieu nay giai thich chi tiet tung folder/file trong project `selenium_enterprise_framework`. Muc tieu la giup nguoi moi vao project hieu nhanh: file nam dau, dung de lam gi, khi nao can sua, va no lien quan den luong test nhu the nao.

## 1. Tong quan project

Day la framework automation test UI dung Selenium WebDriver + TestNG + Maven. Project dang test ung dung Sauce Demo theo cac flow chinh:

- Login
- Xem/search san pham
- Filter san pham
- Add to cart
- Checkout

Framework duoc thiet ke theo Page Object Model:

- Test case nam trong `src/test/java/tests`
- Page object nam trong `src/main/java/pages`
- Component dung lai nam trong `src/main/java/components`
- Driver/config/report/utils nam trong `src/main/java`
- Config va test data nam trong `src/main/resources` va `src/test/resources`

## 2. Root project

```text
selenium_enterprise_framework/
├── README.md
├── PROJECT_STRUCTURE_DETAILS.md
├── pom.xml
├── Jenkinsfile
├── docker-compose.yml
├── screenshots/
├── reports/
├── target/
└── src/
```

### `README.md`

File gioi thieu nhanh project:

- Project dung tech stack nao
- Cach chay test
- Cach chon environment
- Tom tat structure

Nen doc file nay dau tien khi clone project.

### `PROJECT_STRUCTURE_DETAILS.md`

File ban dang doc. Day la tai lieu chi tiet hon README, giai thich y nghia tung folder/file de onboard nguoi moi.

### `pom.xml`

File cau hinh Maven. Vai tro:

- Khai bao ten project, version, groupId/artifactId
- Khai bao Java version
- Khai bao dependencies: Selenium, TestNG, WebDriverManager, Jackson, ExtentReports, Apache POI, Log4j2
- Cau hinh `maven-surefire-plugin` de Maven chay TestNG suite o `src/test/resources/testng.xml`

Khi can them thu vien moi, sua version dependency, hoac thay doi cach Maven chay test thi sua file nay.

### `Jenkinsfile`

File pipeline cho Jenkins CI/CD. Dung khi muon Jenkins checkout code, build, va run automation test tu dong.

### `docker-compose.yml`

File Docker Compose. Dung khi project can chay them service/container local, vi du Selenium Grid hoac service phu tro. Hien tai project chinh van chay local browser qua Selenium/WebDriverManager.

### `screenshots/`

Folder artifact sinh ra khi test fail. `TestListener.onTestFailure()` goi `ScreenshotUtils.capture()` de luu anh loi vao day va attach screenshot vao ExtentReports.

Khong nen commit anh fail len Git tru khi can minh hoa bug.

### `reports/`

Folder report, vi du ExtentReports output `reports/extent-report.html`.

### `target/`

Folder build output cua Maven. Maven tao ra khi compile/test:

- `target/classes`
- `target/test-classes`
- `target/surefire-reports`

Neu gap loi class cu/stale compiled class, chay:

```bash
mvn clean test
```

`target/` la generated folder, khong nen commit.

## 3. Source structure

```text
src/
├── main/
│   ├── java/
│   └── resources/
└── test/
    ├── java/
    └── resources/
```

### `src/main/java`

Chua framework code chinh: page objects, driver, config, utils, report, models, components.

Code trong day duoc compile vao `target/classes`.

### `src/main/resources`

Chua resource runtime chinh: config, JSON data, SQL scripts, file upload, log config.

### `src/test/java`

Chua test code: test cases, base test, data providers, hooks.

Code trong day duoc compile vao `target/test-classes`.

### `src/test/resources`

Chua resource rieng cho test run: `testng.xml`, config test, test data, SQL test.

Khi chay `mvn test`, Maven copy test resources vao classpath test. Neu cung ton tai file config o ca `src/main/resources/config` va `src/test/resources/config`, test classpath se co ban test resources de dung trong test run.

## 4. `src/main/java/api`

```text
src/main/java/api/
├── LoginAPI.java
└── ProductAPI.java
```

Package nay danh cho API helper layer. Trong automation framework, API helper thuong duoc dung de:

- Tao test data nhanh hon UI
- Login bang API de lay token/session
- Setup/precondition truoc khi UI test
- Verify backend data sau UI action

### `LoginAPI.java`

Hien tai la helper mau:

- Method `login(String username, String password)` tra ve chuoi token gia lap `token-for-<username>`

Y nghia: cho thay noi dat logic login bang API neu sau nay project co endpoint that.

### `ProductAPI.java`

Hien tai la helper mau:

- Method `fetchProducts()` tra ve JSON string danh sach product gia lap

Y nghia: cho thay noi dat API lay product neu sau nay can setup/verify product data bang API.

## 5. `src/main/java/components`

```text
src/main/java/components/
├── FooterComponent.java
├── HeaderComponent.java
└── ProductCard.java
```

Package nay chua cac UI component dung lai tren nhieu page. Component khac Page Object o cho:

- Page Object dai dien cho mot page/man hinh
- Component dai dien cho mot phan UI co the xuat hien tren nhieu page

### `FooterComponent.java`

Component dai dien footer cua app. Hien tai moi extend `BasePage`, chua co action rieng.

Dung khi sau nay can verify footer text, social links, copyright, link policy.

### `HeaderComponent.java`

Component dai dien header cua app.

Vai tro hien tai:

- Locate cart icon/link bang `.shopping_cart_link`
- Method `openCart()` click vao cart
- Neu click fail, fallback bang direct URL tu `/inventory.html` sang `/cart.html`

Dung trong checkout flow de di tu product page/cart icon sang cart page.

### `ProductCard.java`

Component dai dien mot product card theo ten product.

Vai tro:

- Tim product theo ten
- Check product card co visible khong
- Click button add/remove tren product item

Dung khi muon thao tac voi san pham cu the thay vi click product dau tien.

## 6. `src/main/java/config`

```text
src/main/java/config/
├── BrowserFactory.java
├── ConfigLoader.java
├── Constants.java
├── DriverFactory.java
└── Environment.java
```

Package nay la trung tam cau hinh framework.

### `ConfigLoader.java`

Doc file config theo environment.

Logic chinh:

```java
String env = System.getProperty("env", Constants.DEFAULT_ENV);
String fileName = env + ".properties";
getResourceAsStream("config/" + fileName)
```

Y nghia:

- Chay `mvn test` thi dung default env
- Chay `mvn test -Denv=qa` thi load `config/qa.properties`
- Chay `mvn test -Denv=staging` thi load `config/staging.properties`
- Chay `mvn test -Denv=prod` thi load `config/prod.properties`

Method quan trong:

- `getBaseUrl()`
- `getBrowser()`
- `isHeadless()`
- `getProperty(String key)`

### `Constants.java`

Chua hang so dung chung:

- `ENV_QA`
- `ENV_STAGING`
- `ENV_PROD`
- `DEFAULT_ENV`
- `BASE_URL`
- `BROWSER`
- `HEADLESS`

Default environment hien tai la `qa`.

### `DriverFactory.java`

Factory quan ly driver manager bang `ThreadLocal`.

Vai tro:

- `initializeDriver()`: tao `LocalDriverManager`
- `getDriver()`: lay WebDriver hien tai
- `quitDriver()`: quit browser va remove ThreadLocal

Dung trong `BaseTest.setUp()` va `BaseTest.tearDown()`.

### `BrowserFactory.java`

Factory tao WebDriver theo browser:

- Chrome
- Firefox
- Edge

Co cau hinh ChromeOptions:

- Tat password manager popup
- Tat automation extension
- Ho tro headless mode
- Set window size/start maximized

`BrowserFactory` la noi tap trung logic tao browser. `LocalDriverManager` doc config, parse browser thanh `BrowserType`, roi delegate viec tao browser cho factory nay.

### `Environment.java`

Enum khai bao environment:

- `QA`
- `STAGING`
- `PROD`

Huu ich khi muon type-safe environment thay vi string.

## 7. `src/main/java/data`

```text
src/main/java/data/
└── TestDataLoader.java
```

### `TestDataLoader.java`

Lop load test data tu JSON resource.

Method:

- `loadLoginData()`: doc `testdata/login.json`
- `loadProducts()`: doc `testdata/products.json`

Ben trong dung `JsonUtils.read(...)`.

Dung khi test data lon hon va khong muon hard-code data trong test case.

## 8. `src/main/java/database`

```text
src/main/java/database/
├── DBConnection.java
└── UserRepository.java
```

Package danh cho database helper/repository.

### `DBConnection.java`

Hien tai tra ve connection string mau:

```text
jdbc:h2:mem:testdb
```

Y nghia: vi tri de sau nay cau hinh DB connection that neu test can setup/verify database.

### `UserRepository.java`

Repository mau cho user data.

Hien tai:

- `findAll()` tra ve empty list

Y nghia: skeleton cho logic query user tu DB sau nay.

## 9. `src/main/java/driver`

```text
src/main/java/driver/
├── DriverManager.java
├── LocalDriverManager.java
└── RemoteDriverManager.java
```

Package nay quan ly lifecycle cua WebDriver.

### `DriverManager.java`

Abstract base class cho driver manager.

Vai tro:

- Giu WebDriver trong `ThreadLocal`
- Lazy create driver khi `getDriver()` duoc goi
- Quit va remove driver khi test ket thuc

`ThreadLocal` giup framework san sang hon cho parallel test.

### `LocalDriverManager.java`

Tao browser local tren may dang run test.

Vai tro:

- Doc browser/headless tu `ConfigLoader`
- Delegate tao Chrome/Firefox/Edge driver cho `BrowserFactory`
- Giu class nay gon, chi quan tam local driver lifecycle

Day la driver manager dang duoc `DriverFactory` su dung.

### `RemoteDriverManager.java`

Danh cho Selenium Grid/remote browser.

Dung khi muon run test tren remote machine/container/cloud browser thay vi local browser.

## 10. `src/main/java/enums`

```text
src/main/java/enums/
├── BrowserType.java
└── UserRole.java
```

Package chua enum dung chung.

### `BrowserType.java`

Enum loai browser:

- `CHROME`
- `FIREFOX`
- `EDGE`

Co method `from(String value)` de parse browser config tu `.properties`.

Y nghia:

- Tranh typo nhu `chorme`
- Validate browser config som
- Giam stringly-typed code trong driver setup
- Lam `BrowserFactory` va `LocalDriverManager` ro rang hon

### `UserRole.java`

Enum role cua user. Huu ich khi test can phan quyen user, vi du admin/customer/staff.

## 11. `src/main/java/listeners`

```text
src/main/java/listeners/
├── RetryListener.java
└── TestListener.java
```

Package chua TestNG listener.

### `TestListener.java`

Implement `ITestListener`.

Hien tai:

- Tao ExtentReports test node khi test start
- Log pass/fail/skip vao report
- Khi test fail, lay WebDriver tu test instance
- Chup screenshot bang `ScreenshotUtils`
- Attach screenshot vao ExtentReports
- Luu HTML page source vao `screenshots`
- In ten test fail ra console
- Flush ExtentReports khi suite finish

Co the mo rong de:

- Them metadata env/browser vao report
- Attach video/log file neu co

### `RetryListener.java`

Implement `IAnnotationTransformer`.

Vai tro:

- Gan `RetryAnalyzer` vao test method de retry khi fail
- Chi apply retry khi `testMethod != null`, tranh tac dong nham vao configuration annotations

Duoc khai bao trong `src/test/resources/testng.xml`.

## 12. `src/main/java/models`

```text
src/main/java/models/
├── Product.java
└── User.java
```

Package chua POJO/domain model.

### `User.java`

Model dai dien user test data. Thuong co cac field nhu username, password, role.

### `Product.java`

Model dai dien product test data. Thuong co field nhu name, price, description.

Model giup mapping JSON/API response thanh object thay vi dung Map/String raw.

## 13. `src/main/java/pages`

```text
src/main/java/pages/
├── BasePage.java
├── CartPage.java
├── CheckoutPage.java
├── HomePage.java
├── LoginPage.java
└── ProductPage.java
```

Package chua Page Object Model. Moi class dai dien cho mot page/man hinh cua application.

### `BasePage.java`

Base class cho tat ca page/component.

Chua:

- `driver`
- `WebDriverWait`
- `waitForVisible(By locator)`
- `waitForClickable(By locator)`
- `isDisplayed(By locator)`
- `click(By locator)`
- `type(By locator, String text)`
- `textOf(By locator)`

Y nghia:

- Gom logic wait/click/type dung chung
- Giam duplicate code trong cac page object
- Xu ly popup/overlay truoc khi click/type
- Fallback JavaScript click khi Selenium click bi block

Khi page object can thao tac UI, nen dung helper trong `BasePage` thay vi goi truc tiep `driver.findElement(...)` qua nhieu noi.

### `LoginPage.java`

Page object cho login page.

Locators:

- username input
- password input
- login button
- error message

Method:

- `open()`: mo Sauce Demo
- `login(username, password)`: nhap username/password va click login
- `isErrorVisible()`: check loi login co hien khong

### `HomePage.java`

Page object cho inventory/home page sau login.

Method:

- `isInventoryVisible()`: check danh sach product co hien khong
- `getTitle()`: lay title cua page

### `ProductPage.java`

Page object cho product/inventory page.

Method:

- `searchProduct(String productName)`: verify product name co trong inventory list
- `filterBy(String option)`: chon option trong sort dropdown
- `addFirstProductToCart()`: click add-to-cart san pham dau tien
- `isCartBadgeVisible()`: check badge cart sau khi add product

### `CartPage.java`

Page object cho cart page.

Method:

- `open()`: di toi cart page bang URL replacement
- `checkout()`: click checkout va wait toi khi checkout form hien ra

Class nay co fallback direct navigation neu click checkout bi block/flaky.

### `CheckoutPage.java`

Page object cho checkout flow.

Method:

- `fillCustomerInfo(first, last, zip)`: nhap thong tin checkout va click continue
- `finish()`: click finish
- `isComplete()`: verify checkout complete page hien ra

Co guard `ensureCheckoutStepOneLoaded()` de dam bao dang o checkout step one truoc khi type.

## 14. `src/main/java/report`

```text
src/main/java/report/
├── ExtentManager.java
└── ExtentTestManager.java
```

Package quan ly report.

### `ExtentManager.java`

Tao singleton `ExtentReports`.

Output report:

```text
reports/extent-report.html
```

### `ExtentTestManager.java`

Giu `ExtentTest` trong `ThreadLocal`.

Y nghia:

- Moi test/thread co report node rieng
- San sang hon cho parallel execution

## 15. `src/main/java/utils`

```text
src/main/java/utils/
├── ExcelUtils.java
├── JsonUtils.java
├── Logger.java
├── PopupHandler.java
├── RandomDataUtils.java
├── RetryAnalyzer.java
├── ScreenshotUtils.java
└── WaitUtils.java
```

Package chua utility dung chung.

### `JsonUtils.java`

Doc JSON tu classpath resource va parse thanh `Map<String, Object>` bang Jackson.

Dung boi `TestDataLoader`.

### `ExcelUtils.java`

Helper doc Excel. Hien tai la stub tra ve `"sample"`.

Y nghia: noi de implement doc file `.xlsx` bang Apache POI neu test data duoc quan ly trong Excel.

### `Logger.java`

Logging wrapper. Dung de gom logic log vao mot noi thay vi dung `System.out.println` rai rac.

### `PopupHandler.java`

Helper xu ly popup/dialog/overlay co the block UI automation.

Vai tro:

- Tim cac button nhu no thanks, not now, later, dismiss, close, skip, ok
- Gui ESCAPE
- Tim dialog co text password manager/change password
- Hide overlay bang JavaScript neu can

Luu y quan trong:

- Helper nay phai can than de khong click nham button cua app that.
- Khong nen them selector qua generic nhu `cancel`, vi co the click nham nut Cancel tren checkout page.

### `RandomDataUtils.java`

Helper tao random data cho test, vi du random email/name/string.

Dung khi test can data unique.

### `RetryAnalyzer.java`

Implement TestNG `IRetryAnalyzer`.

Vai tro:

- Cho phep retry test khi fail
- Hien tai max retry la 1 lan

Duoc gan vao test thong qua `RetryListener`.

### `ScreenshotUtils.java`

Capture screenshot khi test fail.

Output:

```text
screenshots/<testName>_<timestamp>.png
```

Duoc goi trong `TestListener.onTestFailure()`.

### `WaitUtils.java`

Helper wait rieng:

- `waitForVisible(WebDriver driver, By locator)`

Co the dung cho code khong extend `BasePage`.

## 16. `src/main/resources`

```text
src/main/resources/
├── config/
├── files/
├── log4j2.xml
├── sql/
└── testdata/
```

### `src/main/resources/config`

Chua config runtime:

- `qa.properties`
- `staging.properties`
- `prod.properties`

Moi file co:

```properties
browser=chrome
headless=false
base.url=https://www.saucedemo.com/
```

### `src/main/resources/testdata`

Chua JSON data:

- `login.json`
- `products.json`

Dung cho test data loader hoac utility doc JSON.

### `src/main/resources/sql`

Chua SQL scripts:

- `createUser.sql`
- `deleteUser.sql`

Dung khi framework can setup/cleanup DB data.

### `src/main/resources/files`

Chua file fixture:

- `upload.pdf`

Dung cho test upload file neu co.

### `src/main/resources/log4j2.xml`

Config logging cua Log4j2.

Dung de cau hinh log level, pattern, output console/file.

## 17. `src/test/java/base`

```text
src/test/java/base/
└── BaseTest.java
```

### `BaseTest.java`

Base class cho test cases.

`setUp()`:

- Khoi tao driver qua `DriverFactory.initializeDriver()`
- Lay driver qua `DriverFactory.getDriver()`
- Tao `LoginPage`
- Maximize browser
- Mo login page
- Dismiss popup neu co

`getDriver()`:

- Expose active WebDriver cho listener lay driver va capture failure artifacts

`tearDown()`:

- Quit driver

Luu y: failure artifacts nhu screenshot va HTML source duoc xu ly trong `TestListener`, khong nam trong `BaseTest`. Cach nay giup flow fail ro rang hon:

```text
Test fail -> TestListener.onTestFailure() -> capture artifacts -> update report -> BaseTest.tearDown() quit browser
```

Moi test class nen extend `BaseTest`.

## 18. `src/test/java/dataprovider`

```text
src/test/java/dataprovider/
└── UserDataProvider.java
```

### `UserDataProvider.java`

Chua TestNG data provider.

Hien tai:

- Data provider `loginUsers`
- Tra ve user `standard_user` va password `secret_sauce`

Dung trong `LoginTest`.

## 19. `src/test/java/hooks`

```text
src/test/java/hooks/
└── BeforeAfterMethod.java
```

### `BeforeAfterMethod.java`

Hook TestNG mau:

- `beforeMethod()`: in message bat dau test
- `afterMethod()`: in message ket thuc test

Co the dung de them logging chung truoc/sau moi test method.

## 20. `src/test/java/tests`

```text
src/test/java/tests/
├── cart/
├── checkout/
├── login/
└── product/
```

Package chua test cases. Nen chia theo business feature.

### `src/test/java/tests/login/LoginTest.java`

Test login thanh cong.

Flow:

1. Login bang data provider
2. Tao `HomePage`
3. Verify inventory list hien thi

### `src/test/java/tests/product/SearchProductTest.java`

Test user thay product trong inventory.

Flow:

1. Login
2. Tao `ProductPage`
3. Search/verify product Backpack
4. Assert page source co `Sauce Labs Backpack`

### `src/test/java/tests/product/FilterProductTest.java`

Test filter/sort product theo gia.

Flow:

1. Login
2. Chon sort option `Price (low to high)`
3. Verify page co `Sauce Labs Bike Light`

### `src/test/java/tests/cart/AddCartTest.java`

Test add san pham vao cart.

Flow:

1. Login
2. Add product dau tien vao cart
3. Verify cart badge hien thi

### `src/test/java/tests/checkout/CheckoutTest.java`

Test checkout end-to-end.

Flow:

1. Login
2. Add product dau tien
3. Mo cart tu header
4. Click checkout
5. Nhap thong tin customer
6. Finish checkout
7. Verify complete page hien thi

## 21. `src/test/resources`

```text
src/test/resources/
├── config/
├── files/
├── sql/
├── testdata/
└── testng.xml
```

### `src/test/resources/testng.xml`

Suite file cua TestNG.

Vai tro:

- Khai bao listeners:
  - `listeners.TestListener`
  - `listeners.RetryListener`
- Khai bao danh sach test classes Maven se run

Maven Surefire trong `pom.xml` tro toi file nay.

### `src/test/resources/config`

Config rieng cho test run:

- `qa.properties`
- `staging.properties`
- `prod.properties`

Khi run `mvn test`, `ConfigLoader` doc config tu classpath. Cac file trong test resources co san trong test classpath.

### `src/test/resources/testdata`

Test data JSON dung rieng cho test.

### `src/test/resources/sql`

SQL scripts dung trong test setup/cleanup.

### `src/test/resources/files`

File fixture dung trong test, vi du upload file.

## 22. Luong chay test khi goi `mvn test`

```text
mvn test
  -> Maven Surefire plugin
  -> src/test/resources/testng.xml
  -> TestNG listeners
  -> Test class
  -> BaseTest.setUp()
  -> DriverFactory
  -> LocalDriverManager
  -> ConfigLoader
  -> Page objects
  -> Assertions
  -> BaseTest.tearDown()
```

Chi tiet:

1. Maven doc `pom.xml`.
2. Surefire plugin lay suite `src/test/resources/testng.xml`.
3. TestNG load listeners.
4. TestNG tao test class.
5. `BaseTest.setUp()` mo browser va login page.
6. Test method goi page object de thao tac UI.
7. Assertion verify ket qua.
8. Neu fail, `TestListener.onTestFailure()` capture screenshot, luu HTML source, va attach screenshot vao report.
9. Driver quit.

## 23. Cach chon environment

Default:

```bash
mvn test
```

Chay QA:

```bash
mvn test -Denv=qa
```

Chay staging:

```bash
mvn test -Denv=staging
```

Chay prod:

```bash
mvn test -Denv=prod
```

Config duoc load theo format:

```text
config/<env>.properties
```

Vi du:

```text
config/qa.properties
```

## 24. Khi nao nen sua file nao?

- Them test case moi: tao file trong `src/test/java/tests/<feature>`
- Them page moi: tao class trong `src/main/java/pages`
- Them component dung lai: tao class trong `src/main/java/components`
- Sua browser/headless/base URL: sua file trong `src/test/resources/config` hoac `src/main/resources/config`
- Them data JSON: them file trong `src/test/resources/testdata` hoac `src/main/resources/testdata`
- Them data provider: tao/sua file trong `src/test/java/dataprovider`
- Them wait/click/type behavior chung: sua `BasePage`
- Sua driver setup: sua `DriverFactory`, `LocalDriverManager`, hoac `BrowserFactory`
- Sua retry behavior: sua `RetryAnalyzer` hoac `RetryListener`
- Sua report: sua `ExtentManager`, `ExtentTestManager`, hoac listener lien quan
- Sua screenshot/failure artifact: sua `ScreenshotUtils` hoac `TestListener.onTestFailure()`

## 25. Convention nen follow

- Test class nen extend `BaseTest`.
- Page object nen extend `BasePage`.
- Khong hard-code wait bang `Thread.sleep` neu co the dung explicit wait.
- Locator nen de trong page object/component, khong nen rai trong test.
- Test nen doc nhu business flow, khong nen chua qua nhieu Selenium detail.
- Resource data nen dat trong `src/test/resources` neu chi phuc vu test.
- Generated folder nhu `target`, `screenshots`, `reports` khong nen commit tru khi project co yeu cau rieng.
