package main

import (
	"context"
	"github.com/sirupsen/logrus"
	"os"
	"poc-db-on-memory/mem_db"
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
	logger.Infof("Exiting application")
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
	logrus.SetLevel(logrus.DebugLevel)
	return logrus.WithContext(ctx)
}
