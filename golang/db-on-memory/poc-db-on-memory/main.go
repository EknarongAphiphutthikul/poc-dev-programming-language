package main

import (
	"context"
	"github.com/sirupsen/logrus"
	"os"
	"poc-db-on-memory/file"
	"poc-db-on-memory/mem_db"
	"runtime"
	"time"
)

var (
	ctx                   = context.Background()
	logger                = initLogRus(ctx)
	dbServer mem_db.MemDB = nil
)

func init() {
	logger.Infof("Application initializing.....")
	dbPort := 3306
	dbName := "demo"
	dbServer = startDatabaseOnMemory(ctx, dbPort, dbName)
}

func destroy() {
	logger.Infof("Destroying.....")
	if nil != dbServer {
		logger.Infof("Shutting down database server")
		_ = dbServer.ConnClose()
		dbServer.EngineClose()
	}
	logger.Infof("Destroy successfully")
}
func main() {
	logger.Infof("Starting application")
	defer destroy()
	monitorMemoryUsage()
	process()
	logger.Infof("Exiting application")
}

func monitorMemoryUsage() {
	ticker := time.NewTicker(5 * time.Second)
	go func() {
		for range ticker.C {
			PrintMemUsage()
		}
	}()
}

func process() {
	err := execCreateTable()
	if err != nil {
		logger.Errorf("ExecCreateTable Error %v", err)
	}

	sourceFile := ""
	err = readSqlStatementFromFileAndExecInsertData(sourceFile)
	if err != nil {
		logger.Errorf("ReadSqlStatementFromFileAndExecInsertData Error %v", err)
	}

	err = checkDataAfterInsertData()
	if err != nil {
		logger.Errorf("CheckDataAfterInsertData Error %v", err)
	}
}

func checkDataAfterInsertData() error {
	t1 := time.Now()
	data, err := dbServer.Query("")
	if err != nil {
		return err
	}
	defer func() { _ = data.Close() }()

	for data.Next() {
		var countData int
		err = data.Scan(&countData)
		if err != nil {
			return err
		}
		logger.Infof("checkDataAfterInsertData Time %v, countData %v", time.Since(t1), countData)
	}
	return nil
}

func execCreateTable() error {
	_, err := dbServer.Exec("")
	return err
}

func execTruncate() error {
	t1 := time.Now()
	_, err := dbServer.Exec("")
	logger.Infof("ExecTruncate Time %v", time.Since(t1))
	return err
}

func readSqlStatementFromFileAndExecInsertData(sourceFile string) error {
	t1 := time.Now()
	maximumBufferSize := 1024 * 1024 * 2 // 2MB
	sourceOS, err := file.OpenFile(sourceFile, os.O_RDONLY)
	if err != nil {
		return err
	}
	defer func() { _ = sourceOS.Close() }()

	scanner := file.InitialBufferScanner(sourceOS, maximumBufferSize)
	for scanner.Scan() {
		err = execTruncate()
		if err != nil {
			logger.Errorf("ExecTruncate Error %v", err)
		}
		err = execInsertData(scanner.Text())
		if err != nil {
			logger.Errorf("ExecInsertData Error %v", err)
		}
		err = checkDataAfterInsertData()
		if err != nil {
			logger.Errorf("CheckDataAfterInsertData Error %v", err)
		}
		err = execQueryData()
		if err != nil {
			logger.Errorf("ExecQueryData Error %v", err)
		}
		err = execQueryIntoOutFile()
		if err != nil {
			logger.Errorf("ExecQueryIntoOutFile Error %v", err)
		}
	}
	logger.Infof("ReadSqlStatementFromFileAndExecInsertData Time %v", time.Since(t1))
	return nil
}

func execQueryIntoOutFile() error {
	t1 := time.Now()
	_, err := dbServer.Exec("")
	logger.Infof("ExecQueryIntoOutFile Time %v", time.Since(t1))
	return err
}

func execQueryData() error {
	t1 := time.Now()
	data, err := dbServer.Query("")
	if err != nil {
		return err
	}
	defer func() { _ = data.Close() }()
	var dataKey string
	for data.Next() {
		err = data.Scan(&dataKey)
		if err != nil {
			return err
		}
	}
	logger.Infof("execQueryData Time %v", time.Since(t1))
	return nil
}

func execInsertData(statement string) error {
	t1 := time.Now()
	_, err := dbServer.Exec(statement)
	logger.Infof("ExecInsertData Time %v", time.Since(t1))
	return err
}

func startDatabaseOnMemory(ctx context.Context, dbPort int, dbName string) mem_db.MemDB {
	logger.Infof("Starting database server on memory port %d with db name %s", dbPort, dbName)
	db := mem_db.NewOnMemMySQL(ctx, dbPort, dbName)
	err := db.EngineStart()
	if nil != err {
		panic(err)
	}
	err = db.ConnOpen()
	if nil != err {
		panic(err)
	}
	return db
}

func initLogRus(ctx context.Context) logrus.FieldLogger {
	logrus.SetFormatter(&logrus.JSONFormatter{})
	logrus.SetOutput(os.Stdout)
	logrus.SetLevel(logrus.InfoLevel)
	return logrus.WithContext(ctx)
}

func bToMb(b uint64) uint64 {
	return b / 1024 / 1024
}

func PrintMemUsage() {
	var m runtime.MemStats
	runtime.ReadMemStats(&m)
	logger.Infof("Alloc = %v MiB\tTotalAlloc = %v MiB\tSys = %v MiB\tHeapAlloc = %v MiB\tHeapInuse = %v MiB\tHeapIdle = %v MiB\tHeapReleased = %v MiB\tNumGC = %v\n",
		bToMb(m.Alloc),
		bToMb(m.TotalAlloc),
		bToMb(m.Sys),
		bToMb(m.HeapAlloc),
		bToMb(m.HeapInuse),
		bToMb(m.HeapIdle),
		bToMb(m.HeapReleased),
		m.NumGC)
}
