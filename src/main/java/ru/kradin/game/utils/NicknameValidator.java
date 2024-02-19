package ru.kradin.game.utils;

public class NicknameValidator {
    private static String allowedCharacters = "^[a-zA-Z0-9а-яА-Я._ -]{3,}$";
    public static boolean isValid(String nickname) {
        return nickname.matches(allowedCharacters) && nickname.length() <= 20;
    }

    public static String eliminateUnnecessarySpaces(String nickname) {
        // Удаление пробелов в начале и в конце строки
        nickname = nickname.trim();

        // Разделение строки на слова
        String[] words = nickname.split("\\s+");
        StringBuilder result = new StringBuilder();

        // Обработка каждого слова без лишних пробелов
        for (int i = 0; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                result.append(words[i]);
                if (i < words.length - 1) {
                    result.append(" ");
                }
            }
        }

        return result.toString();
    }

    public static String getValidationRules() {
        return "Никнейм должен состоять из 3-20 разрешённых символов.\n(a-zA-Z0-9а-яА-Я._ -)";
    }
}
