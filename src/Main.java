import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // b = 14 seçiyoruz (m = 16384 kova). 
        // Endüstri standardı olan bu değer, yaklaşık %0.81 hata payı sunar.
        HyperLogLog hll_A = new HyperLogLog(14);
        HyperLogLog hll_B = new HyperLogLog(14);

        // HLL'in tahminini kıyaslamak için gerçek benzersiz eleman sayısını tutan klasik yapı
        Set<Long> gercekKume = new HashSet<>();

        System.out.println("--- 1. Aşama: Veri Akışlarını Simüle Etme ---");

        // Senaryo: 1. Sunucuya 1'den 100.000'e kadar olan kullanıcı ID'leri geliyor
        for (long i = 1; i <= 100000; i++) {
            long hashedValue = HashUtils.hash64(i);
            hll_A.add(hashedValue);
            gercekKume.add(i);
        }

        // Senaryo: 2. Sunucuya 50.000'den 150.000'e kadar olan kullanıcı ID'leri geliyor
        // Dikkat: 50.000 ile 100.000 arasındaki ID'ler her iki sunucuya da gitmiş ortak elemanlardır.
        for (long i = 50000; i <= 150000; i++) {
            long hashedValue = HashUtils.hash64(i);
            hll_B.add(hashedValue);
            gercekKume.add(i);
        }

        System.out.println("A Kümesi (1. Sunucu) HLL Tahmini: " + hll_A.count());
        System.out.println("B Kümesi (2. Sunucu) HLL Tahmini: " + hll_B.count());

        System.out.println("\n--- 2. Aşama: HLL Yapılarını Birleştirme (Merge) ---");

        // 2. Sunucudaki verileri 1. Sunucunun HLL yapısı üzerine ekliyoruz
        hll_A.merge(hll_B);

        // Ortaya çıkan son tahmin
        long hllTahmini = hll_A.count();
        // Set yapısının bildiği kesin ve gerçek benzersiz eleman sayısı (Kesişim birleşimi)
        long gercekBoyut = gercekKume.size();

        // Yüzdelik hata oranı hesaplaması: |Tahmin - Gerçek| / Gerçek * 100
        double hataYuzdesi = Math.abs((double)(hllTahmini - gercekBoyut) / gercekBoyut) * 100;

        System.out.println("Gerçek Kesişimli Toplam Eleman Sayısı: " + gercekBoyut);
        System.out.println("Birleştirilmiş HLL Tahmini:          " + hllTahmini);
        System.out.printf("Hata Oranı:                          %%%.3f\n", hataYuzdesi);
    }
}