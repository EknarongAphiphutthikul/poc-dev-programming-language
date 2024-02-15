package file

import (
	"bufio"
	"bytes"
	"encoding/csv"
	"errors"
	"fmt"
	"os"
)

var endOfLine = []byte("\n")

func OpenFile(file string, flag int) (*os.File, error) {
	os, err := os.OpenFile(file, flag, os.ModePerm)
	if err != nil {
		return nil, err
	}
	return os, nil
}

func CreateFile(path string, file string) (*os.File, error) {
	if err := CheckDir(path); err != nil {
		return nil, err
	}
	os, err := os.Create(path + file)
	if err != nil {
		return nil, err
	}
	return os, nil
}

func WriteLine(os *os.File, lineBytes *[]byte) error {
	_, err := os.Write(append(*lineBytes, endOfLine...))
	if err != nil {
		return err
	}
	return nil
}

func WriteCsv(t *csv.Writer, value [][]string) error {
	if err := t.WriteAll(value); err != nil {
		return err
	}
	return nil
}

func WriteLineStartWith(os *os.File, start *[]byte, lineByte *[]byte, isEnd bool) error {
	if isEnd {
		_, err := os.Write(append(append(*start, (*lineByte)[:len(*lineByte)-1]...), endOfLine...))
		if err != nil {
			return err
		}
	} else {
		_, err := os.Write(append(*start, (*lineByte)[:len(*lineByte)-1]...))
		if err != nil {
			return err
		}
	}
	return nil
}

func InitialBufferScanner(os *os.File, bufferSize int) *bufio.Scanner {
	var buffer []byte
	scanner := bufio.NewScanner(os)
	scanner.Buffer(buffer, bufferSize)
	return scanner
}

func StartWith(lineBytes *[]byte, pattern *[]byte) bool {
	if len(*pattern) > 1 {
		return bytes.Equal(*pattern, (*lineBytes)[0:len(*pattern)])
	} else {
		return (*pattern)[0] == (*lineBytes)[0]
	}
}

func EndWith(lineBytes *[]byte, pattern *[]byte) bool {
	if len(*pattern) > 1 {
		return bytes.Equal(*pattern, (*lineBytes)[len(*lineBytes)-len(*pattern):])
	} else {
		return (*pattern)[0] == (*lineBytes)[len(*lineBytes)-1]
	}
}

func Contain(lineBytes *[]byte, pattern *[]byte) bool {
	return bytes.Contains(*lineBytes, *pattern)
}

func CheckDir(path string) error {
	if IsFileNotExists(path) {
		if err := os.MkdirAll(path, os.ModePerm); err != nil {
			return err
		}
	}
	return nil
}

func IsFileNotExists(path string) bool {
	_, err := os.Stat(path)
	return errors.Is(err, os.ErrNotExist)
}

func ListFile(path string) ([]string, error) {
	var listFile []string
	files, err := os.ReadDir(path)
	if err != nil {
		return nil, err
	}
	for _, file := range files {
		if !file.IsDir() {
			listFile = append(listFile, fmt.Sprintf("%s/%s", path, file.Name()))
		}
	}
	return listFile, nil
}

func DeleteFile(path string) error {
	if err := os.Remove(path); err != nil {
		if err := os.RemoveAll(path); err != nil {
			return err
		}
	}
	return nil
}

func RenameFile(originalName string, newName string) error {
	err := os.Rename(originalName, newName)
	if err != nil {
		return err
	}
	return nil
}

func DeleteEmptyFile(path string) error {
	files, err := ListFile(path)
	if err != nil {
		return err
	}
	for _, file := range files {
		if IsEmptyFile(file) {
			if err := DeleteFile(file); err != nil {
				return err
			}

		}
	}
	return nil
}

func IsEmptyFile(fileNames string) bool {
	file, err := os.Open(fileNames)
	if err != nil {
		return false
	}
	defer file.Close()

	stat, err := file.Stat()
	if err != nil {
		return false
	}

	return stat.Size() == 0
}

func GetLineCount(fileName string) int64 {
	file, err := os.Open(fileName)
	if err != nil {
		return 0
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	var count int64
	for scanner.Scan() {
		count++
	}
	return count
}
