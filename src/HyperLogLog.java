public class HyperLogLog {
    private final int b; // Kova indeksini belirlemek için kullanılacak bit sayısı
    private final int m; // Toplam kova sayısı (m = 2^b)
    private final double alphaM; // Hash çakışmalarını dengeleyen teorik düzeltme sabiti

    // Ödev İsteri: Her kovadaki "ardışık sıfır sayısını" takip eden register yapısı.
    // Hafızadan tasarruf etmek için byte dizisi kullanıyoruz (Her kova 1 byte).
    private final byte[] registers;

    public HyperLogLog(int b) {
        if (b < 4 || b > 16) throw new IllegalArgumentException("b değeri 4 ile 16 arasında olmalıdır.");
        this.b = b;
        this.m = 1 << b; // 1 sayısını b kadar sola kaydırmak 2^b işlemini yapar.
        this.registers = new byte[m];
        this.alphaM = calculateAlpha(m);
    }

    // Olasılıksal varyansı dengelemek için literatürde belirlenmiş sabit katsayılar
    private double calculateAlpha(int m) {
        switch (m) {
            case 16: return 0.673;
            case 32: return 0.697;
            case 64: return 0.709;
            default: return 0.7213 / (1.0 + 1.079 / m);
        }
    }

    /**
     * Ödev İsteri: Veriyi alt kümelere ayıran kovalama (bucketing) mekanizması.
     * Bu metod, yeni bir elemanı HLL yapısına ekler.
     */
    public void add(long hashValue) {
        // 1. ADIM: Kovalama (Bucketing)
        // Hash değerinin en solundaki 'b' adet bitini alarak kova indeksini (0 ile m-1 arası) buluyoruz.
        int bucketIndex = (int) (hashValue >>> (64 - b));

        // 2. ADIM: Ardışık Sıfırları Sayma
        // Kova indeksini belirlediğimiz bitleri çöpe atıp, geriye kalan bitlerdeki
        // en soldan başlayan ardışık sıfır (leading zeros) sayısını buluyoruz. +1 ekliyoruz.
        long remainingBits = (hashValue << b) | (1L << (b - 1));
        byte rank = (byte) (Long.numberOfLeadingZeros(remainingBits) + 1);

        // 3. ADIM: Register Güncelleme
        // Sadece bulduğumuz sıfır sayısı, kovadaki mevcut sayıdan BÜYÜKSE güncelliyoruz.
        if (rank > registers[bucketIndex]) {
            registers[bucketIndex] = rank;
        }
    }

    /**
     * Ödev İsteri: Harmonik Ortalama ve düzeltme faktörleri ile küme büyüklüğünü tahmin etme.
     */
    public long count() {
        double harmonicMeanSum = 0;
        int emptyRegisters = 0;

        // Tüm kovaları gezip harmonik ortalama için payda kısmını topluyoruz: sum(2^(-M[j]))
        for (byte register : registers) {
            harmonicMeanSum += Math.pow(2, -register);
            if (register == 0) emptyRegisters++; // Hiç veri gelmemiş boş kovaları sayıyoruz
        }

        // Harmonik Ortalama formülü uygulanarak ham tahmin elde ediliyor
        double estimate = alphaM * m * m / harmonicMeanSum;

        // Ödev İsteri: Küçük veri setleri için düzeltme faktörü (Linear Counting)
        // Eğer tahmin edilen değer (5/2)*m'den küçükse ve boş kovalar varsa algoritma sapabilir.
        // Bu durumu Linear Counting formülü ile düzeltiyoruz.
        if (estimate <= 2.5 * m) {
            if (emptyRegisters > 0) {
                estimate = m * Math.log((double) m / emptyRegisters);
            }
        }

        return Math.round(estimate); // En yakın tam sayıya yuvarlayarak sonucu dönüyoruz
    }

    /**
     * Ödev İsteri: Algoritmanın "birleştirilebilir" (mergeable) özelliği.
     * İki farklı veri akışından (örn. iki farklı sunucudan) gelen HLL yapılarını veri kaybı olmadan birleştirir.
     */
    public void merge(HyperLogLog other) {
        if (this.m != other.m) {
            throw new IllegalArgumentException("Farklı kova sayılarına (m) sahip HLL'ler birleştirilemez!");
        }

        for (int i = 0; i < m; i++) {
            // İki HLL yapısının da i. kovasına bakıyoruz.
            // Hangi kovadaki ardışık sıfır sayısı (rank) daha büyükse, onu alıyoruz.
            // Bu basit 'maksimum alma' işlemi, veri kayıpsız birleştirmenin (union) sırrıdır.
            this.registers[i] = (byte) Math.max(this.registers[i], other.registers[i]);
        }
    }
}