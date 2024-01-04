on convertGraffleFile(outputFile)
  tell application "OmniGraffle"
    set theDocument to front document
    set theFile to "Users:verhasp:github:dmbook:generated:images:" & outputFile
    set svgOutput to theFile & ".svg"
    save theDocument in file svgOutput
    close theDocument
  end tell
end convertGraffleFile

on run argv
   convertGraffleFile(item 1 of argv)
end run
