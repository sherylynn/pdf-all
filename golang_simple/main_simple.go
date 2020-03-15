package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"strconv"
	"strings"
)

var jsonPath = "./progress_cn.json"

func codeTochar(identifier string) string {
	var char_array string
	id_array := strings.Split(identifier, "\\u")
	for _, CharCode := range id_array {
		if len(CharCode) < 1 {
			continue
		}
		true_code, err := strconv.ParseInt(CharCode, 16, 32)
		if err != nil {
			fmt.Println(err)
		}
		char_array += fmt.Sprintf("%c", true_code)
	}
	identifier_cn := char_array
	return identifier_cn
}
func writeProcess(username string, identifier_cn string, pageNum int) {
	_, progressMap := readProgress(username, identifier_cn)
	if progressMap == nil {
		progressMap = make(map[string]map[string]int)
	}
	if progressMap[username] == nil {
		progressMap[username] = make(map[string]int)
	}
	progressMap[username][identifier_cn] = pageNum
	progressJSONStr, err := json.MarshalIndent(progressMap, "", "  ")
	if err != nil {
		fmt.Println(err)
	}
	ioutil.WriteFile(jsonPath, progressJSONStr, 0666)
}
func readProgress(username string, identifier_cn string) (int, map[string]map[string]int) {

	progressJSONFile, err := os.OpenFile(jsonPath, os.O_CREATE, 0666)
	if err != nil {
		panic(err)
	}
	defer progressJSONFile.Close()
	progressJSONContent, err := ioutil.ReadAll(progressJSONFile)
	if err != nil {
		fmt.Println(err)
	}
	progressJSON := string(progressJSONContent)

	progressMap := map[string]map[string]int{}
	json.Unmarshal([]byte(progressJSON), &progressMap)
	pageNum := progressMap[username][identifier_cn]
	return pageNum, progressMap
}

func update_progress(w http.ResponseWriter, r *http.Request) {
	r.ParseForm()
	username := r.Form["username"][0]
	identifier := r.Form["identifier"][0]
	identifier_cn := codeTochar(identifier)
	pageNum, _ := strconv.Atoi(r.Form.Get("page_num"))
	writeProcess(username, identifier_cn, pageNum)
	message := make(map[string]string)
	message["data"] = "ok"
	json.NewEncoder(w).Encode(message)
}

func get_latest_progress(w http.ResponseWriter, r *http.Request) {
	r.ParseForm()
	username := r.Form["username"][0]
	identifier := r.Form["identifier"][0]
	identifier_cn := codeTochar(identifier)
	pageNum, _ := readProgress(username, identifier_cn)
	message := make(map[string]int)
	message["page_num"] = pageNum
	json.NewEncoder(w).Encode(message)
}

func main() {
	http.HandleFunc("/update_progress", update_progress)         //设置访问的路由
	http.HandleFunc("/get_latest_progress", get_latest_progress) //设置访问的路由
	err := http.ListenAndServe(":10000", nil)                    //设置监听的端口
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}
