# LocationSample

# Google Location Services API

* **Google Location Services API** là một phần của **Google Play Services** cung cấp nhiều sức mạnh hơn, framework cao hơn để có thể tự động hóa các tác vụ cung cấp location và quản lý năng lượng tốt hơn.
* Dịch vụ này cũng cung cấp các tính năng mới như phát hiện các Activity hoạt động không có sẵn trong framework API.

### Các chức năng chính của Location Service API

* Lấy vị trí cuối cùng của thiết bị
* Thay đổi cài đặt vị trí
* Nhận sự cập nhật của vị trí
* Hiển thị địa chỉ của vị trí
* Tạo và giám sát các khu vực địa lý trên bản đồ
* Thêm bản đồ vào ứng dụng

# Getting started

## Cài đặt Google Play Service

* Để sử dụng được Google Play Services APIs, bạn cần cài đặt project của mình với **Google Play Services SDK**. Nếu chưa có SDK này, có thể tham khảo cách cập nhật ở [đây](https://developer.android.com/studio/intro/update.html#sdk-manager?hl=en)
* Thêm phụ thuộc của **play-service** vào file build.gradle app-level như sau: 

```
implementation 'com.google.android.gms:play-services:12.0.1'
``` 

* Chắc chắn rằng trong file build.gradle project-level có chứa `google()` repo hoặc là `maven { url "https://maven.google.com" }`.
* Để lưu lại thay đổi thì click vào Sync Project with Gradle Files ở trên toolbar.

## Tối ưu hóa vị trí để tiết kiệm pin

> Việc sử dung vị trí trong ứng dụng lâu dài sẽ khiến ứng dụng gặp phải vấn đề về tiêu thụ năng lượng trong lúc sử dụng. Trong những nỗ lực giảm mức tiêu thụ năng lượng, Android 8.0 (API 26) đã giới hạn tần suất các ứng dụng ở trong background có thể truy xuất vị trí hiện tại của người dùng. Mức này đã được giảm đến chỉ một vài lần mỗi giờ.

### Background Location Limits

Xuất hiện trong Android 8.0 đã giải quyết được vấn để sử dụng dịch vụ location ảnh hưởng đến tiêu hao năng lượng. Có những lợi ích như sau:

* Thu thập vị trí được điều chỉnh và vị trí được tính toán tốt hơn, chỉ được nhận vài lần trong một giờ.
* Quét Wi-Fi thận trọng hơn và cập nhật vị trí không hoạt động khi thiết bị vẫn được kết nối với cùng một điểm truy cập tĩnh.
* Phản ứng vị trí địa lý thay đổi từ 10 giây đến khoảng 2 phút. Sự thay đổi này đáng chú ý giúp cải thiện hiệu suất sử dụng năng lượng tốt hơn đến 10 lần trên một số thiết bị.

### Battery Drain

> Việc sử dụng vị trí và việc tiêu hao pin có liên quan đến mật thiết đến nhau. Nếu độ chính xác càng cao thì hao pin cũng càng lớn. Tương tự vậy tần xuất truy cập vị trí thường xuyên cũng dẫn đến hao pin hơn. Độ trễ cũng không ngoại lệ, nếu muốn độ trễ ít hơn thì phải chấp nhận việc tiêu hao pin nhiều hơn.

Chính vì vậy muốn tiết kiệm pin tiêu thụ, thì phải cải thiện được việc truy xuất vị trí trên ứng dụng theo các phương diện như độ chính xác, tần xuất và độ trễ.

#### Độ chính xác
Bạn có thể chỉnh được độ chính xác của việc xác định vị trí bằng phương thức `setPriority()` với một số các giá trị sau:

* `PRIORITY_HIGH_ACCURACY`: Cung cấp vị trí chính xác nhất có thể bằng cách sử dụng nhiều đầu vào để xác định vị trí. Nó cho phép GPS, Wi-Fi, mang di động và các cảm biến khác nữa dẫn đến hao tổn pin khá lớn.
* `PRIORITY_BALANCED_POWER_ACCURACY`: Cung cấp vị trí chính xác trong khi tối ưu hóa năng lượng. Rất ít khi sử dụng GPS, thông thường sử dụng Wi-Fi và thông tin di động để xác định vị trí.
* `PRIORITY_LOW_POWER`: Chủ yếu dựa vào các tháp di động và tránh sử dụng GPS và Wi-Fi, cung cấp độ chính xác thô cấp thành phố với độ tiêu hao pin tối thiểu.
* `PRIORITY_NO_POWER`: Nhận vị trí thụ động từ các ứng dụng khác mà vị trí đã được tính toán rồi.

Tùy vào ứng dụng cần độ chính xác như nào thì sẽ cấp cho ứng dụng quyền truy cập tương ứng.

#### Tần suất truy cập
Có 2 phương thức cho phép bạn thay đổi khoảng thời gian tính toán vị trí:

* `setInterval()`: Để chỉ định khoản thời gian mà vị trí được tính toán cho ứng dụng của bạn.
* `setFastestInterval`: Để chỉ định khoảng thời gian mà vị trí được tính toán cho các ứng dụng khác được gửi đến ứng dụng của bạn.

Để tránh lãng phí pin thì nên sử dụng khoảng thời gian vài giây để lấy vị trí trong các trường hợp foreground. Còn trong những trường hợp background thì hãy tìm giá trị lớn nhất có thể được. Việc này được Android 8.0 giải quyết nhưng đối với những phiên bản cũ hơn lại là một các tốt.

#### Độ trễ

* Có thể điều chỉnh độ trễ bằng phương thức `setMaxWaitTime()` thường được truyền một giá trị lớn hơn nhiều lần so với khoảng thời gian `setInterval()`. Cài đặt này giúp cho việc trì hoãn phân phối vị trí và cập nhật vị trí.
* Nếu ứng dụng của bạn không ngay lập tức cần cập nhật vị trí thì bạn nên sử dụng giá trị lớn nhất có thể để có thể tiết kiệm pin nhiều nhất.
* Khi bạn sử dụng **geofences**, ứng dụng của bạn nên truyền vào một giá trị lớn vào trong phương thức `setNotificationResponsiveness()`. Giá trị 5 phút hoặc lớn hơn được đề nghị trong trường hợp này.

### Những trường hợp sử dụng

* Người dùng đang hiển thị hoặc phải cập nhật trong foreground: Ví dụ như ứng dụng bản đồ cần cập nhật dữ liệu vị trí liên tục và độ chính xác cao. Vì vậy `setPriority()` cũng nên để gía trị PRIORITY_HIGH_ACCURACY hoặc PRIORITY_BALANCED_POWER_ACCURACY. Còn khoảng thời gian được chỉ định trong phương thức `setInterval()` có thể là vài giây hoặc vài phút(chỉ định khoảng 2 phút hoặc lớn hơn để giảm thiểu việc sử dụng pin).
* Biết vị trí của thiết bị: Ví dụ như ứng dụng thời tiết muốn biết vị trí của thiết bị. Vậy nên sử dụng phương thức `getLastLocation()` để trả về giá trị khả dụng gần đây(có thể trả về null). Sử dụng kết hợp với phương thức `isLocationAvailable()` trả về giá trị true khi vị trí được trả về một cách hợp lý.
* Bắt đầu cập nhật khi ở một vị trí cụ thể nào đó: Sử dụng vị trí địa lý với cập nhật vị trí hợp nhất. Sử dụng trong một khu vực được xác định trước.
* Bắt đầu cập nhật dựa trên trạng thái của người dùng: Chỉ yêu cầu cập nhật khi người dùng đang lái xe hoặc đang đi xe đạp.
* Cập nhật vị trí trong background gắn liền với các khu vực địa lý: Người dùng muốn được thông báo khi thiết bị ở gần một địa điểm nào đó. Sử dụng phương thức `addGeofences(GeofencingRequest, PendingIntent)` với các tham số cụ thể sau:
    
    * Nếu bạn đang theo dõi chuyển động, hãy sử dụng phương thức `setLoiteringDelay()` và cho giá trị khoảng chừng 5 phút hoặc hơn.
    * Sử dụng `setNotificationResponsiveness()` vượt qua giá trị 5 phút. Tuy nhiên hãy cân nhắc sử dụng giá trị 10 phút nếu ứng dụng của bạn có thể quản lý độ trễ thêm trong khả năng phản hồi.
    > Một ứng dụng có thể đăng ký tối đa 100 genfences mỗi lần. Trong trường hợp muốn theo dõi số lượng lớn các địa điểm, nên cân nhắc việc theo dõi các địa điểm lơn ở cấp thành phố chẳng hạn.
* Cập nhật vị trí trong background mà không có thành phần hiển thị: Việc này phải dử dụng đến PRIORITY_NO_POWER vì gần như không sử dụng đến pin nếu có thể, còn không thì cũng phải sử dụng PRIORITY_BALANCED_POWER_ACCURACY hoặc PRIORITY_LOW_POWER. Nếu cần thêm dữ liệu vị trí nên sử dụng thêm phương thức `setFastestInterval` để thụ động lắng nghe ứng dụng khác. Có thể sử dụng phương thức `setInterval()` 10 phút thì hãy để `setMaxWaitTime()` với giá trị từ 30 đến 60 phút.

## Geocoding in Map

> Đây là dịch vụ của Google hỗ trợ việc chuyển đổi địa lý thành kinh độ và vĩ độ, ngược lại chuyển bất kì kinh độ và vĩ độ nào thành các địa chỉ tương ứng.

### Thêm Google Map vào project
1. Thêm dependency vào file build.gradle app-level như sau:

```
implementation 'com.google.android.gms:play-services-maps:16.1.0'
implementation 'com.google.android.gms:play-services-places:16.0.0'
``` 
2. Cấu hình AndroidManifest.xml

Thêm các quyền cần thiết để Google Map sử dụng.
```
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.INTERNET"/>
```
Thêm thẻ meta-data để sử dụng google api key

```
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="@string/google_maps_key"/>
```
3. Tạo thêm file `google_maps_api.xml` trong thư mục value để lưu trữ api key cũng như hướng dẫn tạo key trên google console

```
<resources>
    <!--
    TODO: Before you run your application, you need a Google Maps API key.

    To get one, follow this link, follow the directions and press "Create" at the end:

    https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=19:B6:91:30:95:D4:1F:A0:B8:0E:A6:3E:0C:98:B1:94:4B:1D:05:E3%3Bandroid.thaihn.locationsample

    You can also add your credentials to an existing key, using these values:

    Package name:
    19:B6:91:30:95:D4:1F:A0:B8:0E:A6:3E:0C:98:B1:94:4B:1D:05:E3

    SHA-1 certificate fingerprint:
    19:B6:91:30:95:D4:1F:A0:B8:0E:A6:3E:0C:98:B1:94:4B:1D:05:E3

    Alternatively, follow the directions here:
    https://developers.google.com/maps/documentation/android/start#get-key

    Once you have your key (it starts with "AIza"), replace the "google_maps_key"
    string in this file.
    -->
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve">AIzaSy...</string>
</resources>
```

4. Tạo project trên Google console
 
 * Truy cập vào https://console.developers.google.com/ và chọn project hoặc tạo mới project.
 * Sau khi xong bước trên, google console sẽ tạo cho bạn 1 key mới. Cần vào đó rồi thêm package name và SHA-1 để ứng dụng có thể build được khi sử dụng key đó. Key này sẽ được điền vào bước 3.
 * Tiếp đó truy cập **APIs & Services/Library**, tại đây enable API **Maps SDK for Android** hoặc các API tương ứng cần sử dụng.

5. Xây dựng Activity chứa Google Map

* Trong file giao diện xml, thêm thẻ **fragment** như sau: 
```
<fragment xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:id="@+id/map"
    tools:context=".MapsActivity"
    android:name="com.google.android.gms.maps.SupportMapFragment" />
```

* Trong Activity xử lý hiển thị Map đơn giản như sau:

```
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
```

### Lấy địa chỉ từ Location có sẵn
* Class Geocoder cung cấp cho chúng ta phương thức lấy được các địa điểm từ một vị trí có sẵn `latitude` và `longitude` cũng như là lấy được tọa độ từ tên của địa điểm đó.
* Thực hiện lấy được class Address từ Location như sau:
```
private fun getAddressFromLocation(latitude: Double, longitude: Double): StringBuilder {
    val geocoder = Geocoder(this)
    val results = StringBuilder()
    val maxResult = 10
    
    val listAddress = geocoder.getFromLocation(latitude, longitude, maxResult)

    if (listAddress.size > 0) {
        listAddress.forEachIndexed { index, address ->
            val strAddress = StringBuilder()
            Log.d(TAG, "address: $address")
            strAddress.append("${address.featureName},${address.countryName},${address.adminArea}")
            results.append("---").append(strAddress).append(System.getProperty("line.separator"))
        }
    }
    return results
}
```
> Đối với trường hợp này, chúng ta lấy nhiều nhất là 10 địa chỉ có thể đúng của vị trí cần xác định. Object Address trả về có thể lấy thêm được nhiều trường khác như `locality`, `adminArea`, `countryName`, ...

* Bạn có thể sử dụng event map click để lấy ra được vị trí click sau đó tìm tên cho địa điểm đó như sau:

```
override fun onMapClick(latLng: LatLng?) {
    latLng?.let {
        addMarker(latLng, mMap)
        val address = getAddressFromLocation(latLng.latitude, latLng.longitude)
        geocodingBinding.tvResult.text = address
    }
}
```
