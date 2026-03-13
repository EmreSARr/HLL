# Büyük Veri Analitiği - HyperLogLog (HLL) Tasarımı ve Gerçeklemesi

Bu proje, "Cardinality Estimation" (Küme Büyüklüğü Tahmini) problemini çözmek amacıyla **HyperLogLog (HLL)** algoritmasının sıfırdan ve harici kütüphane kullanılmadan Java ile gerçeklenmesini içermektedir. Proje, "Agentic Kodlama" prensipleri doğrultusunda modüler olarak tasarlanmış ve test edilmiştir.

## 📌 Özellikler ve Ödev İsterleri

* **Yüksek Kaliteli Hash Fonksiyonu:** Veri dağılımındaki yanlılığı önlemek için "Çığ Etkisi" (Avalanche Effect) yüksek olan 64-bit **MurmurHash3** algoritması kullanılmıştır.
* **Kovalama (Bucketing) ve Register Yapısı:** Hash değerinin ilk $b = 14$ biti kullanılarak $m = 16384$ adet kova oluşturulmuş ve her kovadaki maksimum ardışık sıfır sayısı takip edilmiştir.
* **Matematiksel Düzeltmeler:** Tahmin hesaplamasında **Harmonik Ortalama** kullanılmış ve küçük veri setleri ($< 2.5m$) için **Linear Counting** düzeltme faktörü sisteme entegre edilmiştir.
* **Birleştirilebilirlik (Merge):** İki bağımsız HLL yapısının veri kaybı olmadan birleştirilebilmesi sağlanmıştır (Aynı indeksli kovaların maksimum değerleri alınarak).

## 📊 Teorik Analiz ve Hata Sınırları

HLL algoritmasında kova sayısı ($m$) artırıldıkça standart hata oranı (SE) düşer. Hata oranı matematiksel olarak şu formülle ifade edilir:

$$SE \approx \frac{1.04}{\sqrt{m}}$$

Bu projede $b = 14$ seçildiği için $m = 16384$ kova kullanılmıştır. Beklenen teorik hata sınırı yaklaşık **%0.81**'dir.

## 📂 Proje Yapısı

* `HashUtils.java`: MurmurHash3 tabanlı rastgeleleştirme fonksiyonunu içerir.
* `HyperLogLog.java`: HLL algoritmasının çekirdek yapısını; kovalama, tahmin hesaplamaları ve birleştirme (merge) metodlarını barındırır.
* `Main.java`: İki farklı veri akışının simüle edildiği, kesişen kümelerin test edildiği ve hata oranının hesaplandığı ana çalışma dosyasıdır.

## 🚀 Kurulum ve Çalıştırma

1. Proje dosyalarını (`src` klasörü altındaki sınıfları) Java destekli bir IDE'ye (örn. IntelliJ IDEA) aktarın.
2. Projeyi derleyin ve `Main.java` dosyasını çalıştırın (`Run Main.main()`).
3. Konsolda bağımsız tahmin sonuçlarını, birleştirme (merge) işlemini ve nihai hata oranını gözlemleyin.

## 📈 Test Çıktısı (Örnek Senaryo)

```text
--- 1. Aşama: Veri Akışlarını Simüle Etme ---
A Kümesi (1. Sunucu) HLL Tahmini: 99830
B Kümesi (2. Sunucu) HLL Tahmini: 99181

--- 2. Aşama: HLL Yapılarını Birleştirme (Merge) ---
Gerçek Kesişimli Toplam Eleman Sayısı: 150000
Birleştirilmiş HLL Tahmini:          149841
Hata Oranı:                          %0.106