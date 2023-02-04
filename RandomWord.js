function chooseRandWordKey(keys) {
    var i = 0
    keys.forEach(function(elem) {
        i++
    })
    return  $jsapi.random(i);
}

function replaceLetters(word) {
    return word.replace(/[а-яёА-ЯЁ]/gi, " _ ");
}


function checkLetterInWordIndex(letter, word) {
    if (word.toLowerCase().indexOf(letter.toLowerCase()) !== -1){
  //if (word.search(letter)!== -1) {
    return true;
  } else {
      return false;
  }
}



function hasLetter(word, letter) {
    return word.indexOf(letter) !== -1;
}


function hangman(word, letters) {
  var replacedWord = "";
  for (var i = 0; i < word.length; i++) {
    var found = false;
    for(var j = 0; j < letters.length; j++) {
      if (word[i].toLowerCase() === letters[j].toLowerCase()) {
        found = true;
        break;
      }
    }
    if (found) {
      replacedWord += word[i];
    } else {
      replacedWord += " _ ";
    }
    if (word[i] === " ") {
      replacedWord += " ";
    }
  }
  return replacedWord;
}


function hasUnderscore(word) {
  return word.indexOf("_") !== -1;
}

function findSpaces(word) {
  for(var i = 0; i < word.length; i++) {
    if(word[i] === " ") {
    return true;
  } else 
      return false;
 }  
}


function hasWordInsensitive(words, searchWord) {
  var lowerCaseWords = words.map(function(word) {
    return word.toLowerCase();
  });
  var lowerCaseSearchWord = searchWord.toLowerCase();
  for (var i = 0; i < lowerCaseWords.length; i++) {
    if (lowerCaseWords[i] === lowerCaseSearchWord) {
      return true;
    }
  }
  return false;
}


function repeatString(str, repeat) {
    var repeated = "";
    while(repeat--){
        repeated += str;
    }
    return repeated;
}