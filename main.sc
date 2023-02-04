require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: text/text.sc
    module = zenbot-common
    
require: where/where.sc
    module = zenbot-common

require: patterns.sc
    module = zenbot-common    

require: common.js
    module = zenbot-common

require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData
    strict = true

require: RandomWord.js
    
theme: /
    
    state: Правила
        q!: $regex</start>
        script:
            $session = {}
            $client = {}
            $temp = {}
            $response = {}
        intent!: /Давай поиграем
        a: Я умею играть в Виселицу 😊
        a: Правила такие: я загадаю слово, ты будешь отгадывать его по буквам или можешь пробовать отгадать сразу слово. У тебя 6 попыток. Начнём?
        go!: /Правила/Согласен
        
        state: Согласен
            
            state: Да
                q: $agree
                go!: /Игра

            state: Нет
                q: $disagree
                a: Ну и ладно! Если передумаешь — скажи "давай поиграем"
        
            state: Случайные буквы
                q: * @Letter *
                random:
                    a: Я не понял.
                    a: Ничего не пойму. Ты хочешь поиграть?
                    a: А? Что?

        
    state: Игра
        random:
            a: Вот такое слово загадал😊 Жду от тебя букву или слово целиком
            a: Такое слово отгадаешь?😊 Угадывай буквы или пробуй ввести слово целиком
        # сгенерируем случайное слово из справочника и перейдем в стейт /Проверка
        script:
        #присваиваем переменной $session.keys массив из id слов, находящихся в списке HangmanGameData.csv
            $session.keys = Object.keys($HangmanGameData);
        #выбираем случайное значение с помощью функции chooseRandWordKey    
            var word = $HangmanGameData[chooseRandWordKey($session.keys)].value.word
            $session.prevBotWord = word
        #заменяем буквы в слове на _ с помощью функции replace
            //$reactions.answer($session.prevBotWord)
            $reactions.answer($session.prevBotWord.replace(/[а-яёА-ЯЁ]/gi, " _ "))
            $session.chance = 6
            $session.guessedLetters = []
        
        go: /Проверка
    
    state: Проверка 
        script:
         $session.wordTwo = hangman($session.prevBotWord,$session.guessedLetters)
         if(hasUnderscore($session.wordTwo) == true && $session.chance > 0){
            return ($session.wordTwo);
            }
         else
             $reactions.transition("/Проверка/Итог");
        q: * @Letter *
        script: 
            # сохраняем введенную пользователем букву
            $session.letter = $parseTree._Letter;
            var stop = ["стоп","не","закончить","конец"]
            if (hasWordInsensitive(stop,$session.letter) == true) {
                $reactions.transition("/Правила/Согласен/Нет");
                }
            else
            
            if ($session.letter.length == 1){
                $reactions.transition("/Проверка/Буква");}
            else $reactions.transition("/Проверка/Слово")
                
        
        state: Буква
            script:
            #проверяем, есть ли буква в слове
             if (checkLetterInWordIndex($session.letter, $session.prevBotWord) == true) {
                #добавляем букву в список
                 $session.guessedLetters.push($session.letter)
                 $session.wordTwo = hangman($session.prevBotWord,$session.guessedLetters)
                #перезаписываем слово   
                 $session.wordWithLetter = $session.wordTwo
                 $reactions.answer("Да");
                 $reactions.answer($session.wordWithLetter);
                 //$reactions.answer(toPrettyString($session.guessedLetters));
                 if (hasUnderscore($session.wordWithLetter) == false) {
                     $reactions.transition("/Проверка/Итог");
                     return $session.wordWithLetter
                     }
                 else
                     return $session.wordWithLetter
                     return $session.guessedLetters
                     $reactions.transition("/Проверка");
                 }
              else 
                 $session.chance -= 1
                 $session.chanceHearts = (repeatString("❤️", $session.chance))
                 $session.chanceBrokenHearts = (repeatString("💔", 6 - $session.chance))
                 if ($session.chance == 2 || $session.chance == 1 ){
                    $reactions.answer("Конец игры близок! Осталось всего лишь {{$session.chance}} попытки");
                    $reactions.answer($session.chanceHearts + $session.chanceBrokenHearts)
                    return $session.chance
                    $reactions.transition("/Проверка");}
                 else   
                    $reactions.answer("Неа!");
                    $reactions.answer($session.chanceHearts + $session.chanceBrokenHearts)
                    if ($session.chance == 0) {
                            $reactions.answer("Ты проиграл! Слово было: {{$session.prevBotWord}}. Хочешь ещё раз?");
                            $reactions.transition("/Правила/Согласен");
                            return $session.chance
                        }
                        else
                            return $session.chance
                            $reactions.transition("/Проверка");
        
        
        state: Слово 
            script:
             
             if ($session.letter == $session.prevBotWord) {
                 $reactions.answer("Поздравляю, ты выиграл!Хочешь сыграть ещё?");
                 $reactions.transition("/Правила/Согласен");
                 return $session.letter
                }
             
             else
                $session.chance -= 1
                $reactions.answer("Неа!");
                $session.chanceHearts = (repeatString("❤️", $session.chance))
                $session.chanceBrokenHearts = (repeatString("💔", 6 - $session.chance))
                
                if ($session.chance == 2 || $session.chance == 1 ) {
                    $reactions.answer("Конец игры близок! Осталось всего лишь {{$session.chance}} попытки");
                    $reactions.answer($session.chanceHearts + $session.chanceBrokenHearts)
                    return $session.chance
                    $reactions.transition("/Проверка");
                    
                    }
                else 
                    $reactions.answer($session.chanceHearts + $session.chanceBrokenHearts)
                    //$reactions.answer("У тебя осталось {{$session.chance}} попытки ");
                        if ($session.chance == 0) {
                            $reactions.answer("Ты проиграл! Слово было: {{$session.prevBotWord}}. Хочешь ещё раз?");
                            $reactions.transition("/Правила/Согласен");
                            return $session.chance
                        }
                        else
                            return $session.chance
                            $reactions.transition("/Проверка");
            
                 
        state: Итог
            script:
            
                if ($session.wordWithLetter == $session.prevBotWord && $session.chance !== 0){
                    $reactions.answer("Поздравляю! Хочешь поиграть ещё?");
                    $reactions.transition("/Правила/Согласен");
                }
                else
                    $reactions.answer("Ты проиграл!Слово было: {{$session.prevBotWord}}. Хочешь ещё раз?");
                    $reactions.transition("/Правила/Согласен");
                   
               
    
    
    state: NoMatch || noContext = true
        event!: noMatch
        random:
            a: Я не понял.
            a: Что вы имеете в виду?
            a: Ничего не пойму
            a: А? Что?

    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /

