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
		//如果上一步没有获取，就初始化
		progressMap = make(map[string]map[string]int)
	}
	if progressMap[username] == nil {
		//初始化第二层map
		progressMap[username] = make(map[string]int)
	}
	progressMap[username][identifier_cn] = pageNum
	//格式化显示,不能用\t
	progressJSONStr, err := json.MarshalIndent(progressMap, "", "  ")
	if err != nil {
		fmt.Println(err)
	}
	//控制权限0666
	ioutil.WriteFile(jsonPath, progressJSONStr, 0666)
}
func readProgress(username string, identifier_cn string) (int, map[string]map[string]int) {

	progressJSONFile, err := os.OpenFile(jsonPath, os.O_CREATE, 0666)
	//如果不存在就创建文件并且控制权限0666
	if err != nil {
		panic(err)
	}
	defer progressJSONFile.Close()
	progressJSONContent, err := ioutil.ReadAll(progressJSONFile)
	//读取
	if err != nil {
		fmt.Println(err)
	}
	progressJSON := string(progressJSONContent)
	//初始化二维map
	progressMap := map[string]map[string]int{}
	json.Unmarshal([]byte(progressJSON), &progressMap)
	pageNum := progressMap[username][identifier_cn]
	return pageNum, progressMap
}

func update_progress(w http.ResponseWriter, r *http.Request) {
	r.ParseForm()
	//要先r.ParseForm()才能解析
	username := r.Form["username"][0]
	//获取的是数组，需要指定第一个
	identifier := r.Form["identifier"][0]
	identifier_cn := codeTochar(identifier)
	pageNum, _ := strconv.Atoi(r.Form.Get("page_num"))
	//如果不存在需要手动使用 get 来获取
	//如果没有则是0，转化成数字
	writeProcess(username, identifier_cn, pageNum)
	message := make(map[string]string)
	//初始化消息
	message["data"] = "ok"
	//用json输出
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
