package mem_db

import (
	"context"
	"database/sql"
	"fmt"

	sqle "github.com/dolthub/go-mysql-server"
	"github.com/dolthub/go-mysql-server/memory"
	"github.com/dolthub/go-mysql-server/server"
	_ "github.com/go-sql-driver/mysql"
	"github.com/gocraft/dbr/v2"
)

type MemDB interface {
	EngineStart() error
	EngineClose()
	Query(query string) (*sql.Rows, error)
	Exec(query string) (sql.Result, error)
	ConnOpen() error
	ConnClose() error
}

type onMemMySQL struct {
	config *server.Config
	engine *sqle.Engine
	conn   *dbr.Connection
	port   int
	dbName string
}

func NewOnMemMySQL(ctx context.Context, port int, dbName string) MemDB {
	engine := sqle.NewDefault(
		memory.NewDBProvider(createDatabase(ctx, dbName)),
	)

	config := server.Config{
		Protocol: "tcp",
		Address:  fmt.Sprintf("%s:%d", "localhost", port),
	}

	return &onMemMySQL{
		config: &config,
		engine: engine,
		port:   port,
		dbName: dbName,
	}
}

func (m *onMemMySQL) EngineStart() error {
	provider, err := server.NewDefaultServer(*m.config, m.engine)
	if err != nil {
		return err
	}
	go func(server *server.Server) {
		if err := server.Start(); err != nil {
			panic(err)
		}
	}(provider)

	return nil
}

func (m *onMemMySQL) EngineClose() {
	go func(engine *sqle.Engine) {
		engine.Close()
	}(m.engine)
}

func createDatabase(ctx context.Context, dbName string) *memory.Database {
	db := memory.NewDatabase(dbName)
	db.EnablePrimaryKeyIndexes()
	return db
}

func (m *onMemMySQL) Query(query string) (*sql.Rows, error) {
	return m.conn.Query(query)
}

func (m *onMemMySQL) Exec(query string) (sql.Result, error) {
	return m.conn.Exec(query)
}

func (m *onMemMySQL) ConnOpen() error {
	conn, err := dbr.Open("mysql", fmt.Sprintf("no_user:@tcp(%s:%d)/%s?charset=utf8mb4&parseTime=True&loc=Local", "localhost", m.port, m.dbName), nil)
	if err != nil {
		return err
	}
	m.conn = conn
	return nil
}

func (m *onMemMySQL) ConnClose() error {
	return m.conn.Close()
}
