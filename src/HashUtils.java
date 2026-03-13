public class HashUtils {

    /**
     * Ödev İsteri: Yüksek kaliteli bir hash fonksiyonu.
     * Bu metod, MurmurHash3 algoritmasının 64-bit 'fmix' (final mix) adımını uygular.
     * Amacı: Gelen verileri (örneğin 1, 2, 3 gibi ardışık sayıları) "Çığ Etkisi" (Avalanche Effect)
     * yaratarak tamamen rastgele ve homojen dağılmış 64-bitlik sayılara çevirmektir.
     */
    public static long hash64(long key) {
        key ^= key >>> 33;
        key *= 0xff51afd7ed558ccdL;
        key ^= key >>> 33;
        key *= 0xc4ceb9fe1a85ec53L;
        key ^= key >>> 33;
        return key;
    }
}