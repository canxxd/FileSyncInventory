FileSyncInventory Eklentisi
Amaç

FileSyncInventory Minecraft oyuncularının envanter ender chest ve bazı temel durum verilerini (can, açlık, deneyim) kaydedip yüklemesini sağlayan bir veri senkronizasyon eklentisidir Oyuncular sunucuya giriş yaptığında kayıtlı veriler otomatik yüklenir çıkış yaptığında veya belirli aralıklarla otomatik kaydedilir.

 Özellikler
 YAML tabanlı veri kaydı (her oyuncu için ayrı dosya)
 Envanter senkronizasyonu (itemler Base64’e çevrilip kaydedilir)
 Ender chest senkronizasyonu
 Oyuncu sağlık ve açlık verileri kaydedilir
 Deneyim seviyesi & barı saklanır
 Config üzerinden devre dışı bırakılabilen dünyalar (örneğin creative world’de çalışmasın)
 Otomatik kaydetme özelliği (saniye/tick cinsinden ayarlanabilir)
 Folia/Paper uyumlu (asenkron veri yükleme ve global scheduler kullanımı)

Nasıl Çalışır?
Oyuncu sunucuya katıldığında
Eğer configte devre dışı bırakılmış bir dünyada değilse kayıtlı verisi yüklenir
Eğer hiç kaydı yoksa yeni kayıt oluşturulur
Oyuncu çıkış yaptığında
Envanteri ender chest sağlık açlık bilgileri kaydedilir
Eğer otomatik kaydetme aktifse
Belirlenen aralıklarla tüm oyuncuların verileri kaydedilir

Kullanım Alanı
Hub/Lobby sunucuları: Oyuncular farklı modlara geçerken envanterlerinin kaybolmaması için
Survival veya RPG sunucuları Oyuncu ilerlemesini güvenli şekilde saklamak için
Folia/Paper sunucuları Modern asenkron destekli güvenli veri kaydı için


örnek config.yl
# Oyuncu verilerinin saklanacağı dizin yolu sunucunun ana dizinine veya belirli bir mutlak yol üzerinden belirlenebilir
# Bu yolu sunucunun ana klasörüne göre belirtebilirsiniz
# --
# --
# -- 
# bu kısım öncelikle anlatayım kısaca bu kısım sizin bilgisiyarınızın vdsinizin filesynçinventory verilerinin hangi dizinde tutacağınız eğer 2 farklı 
# sunucuya aynı yerdeki veri dosyasına seçerseniz o 2 sunucu arası envanter aktarımı sağlanır bu mysql içermeyen tamamen localde özel barındırmak için yapıldı discord: canreolo
data_path: "C:/Users/can58/Desktop/FileSyn" #can58 yazan yeri bilgisiyarın kullanıcı adını yazınız o kısım değişse yeterlidir.

# bu kısımda ise öncelikle hangi verilerin datalanmasını istemiyorsanız ayarını false yapınız true ise aktarım çalışıyor demektir
sync_data:
  inventory: true
  enderchest: true
  health: true
  food_level: true
  experience: true
  potion_effects: true
  gamemode: true # dünyalar arası geçiş yaparken veya sunucular geçişlerinde gamemode değerlerini koruması yani bir sunucuda survivaldaysan sunucu değiştiğinde gamemodu korur ama false yaparsan korumaz

disabled_worlds: # bu ayar ise filesynç olan sunucuda hangi dünyalarda envanter aktarımı olmaması kısaca seçtiğin dünyada veriler senkrolize olmaz kaydetmez
  - 'duel'

# otomatik olarak  kaydetme ayarlarınız minimum 0.2 önerilir aksi takdirde eklenti çok yük bindiği için bozulabilir
auto_save_interval:
  enabled: true 
  seconds: 0.5
