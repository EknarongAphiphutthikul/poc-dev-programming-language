package service

import "poc-go-mock/model"

type IService interface {
	Call1(param string) error
	Call2(param *string) error
	Call3(param *model.StructModel) error
	Call4(param model.StructModel) error
}

type service struct {
}

func InitService() IService {
	return &service{}
}

func (s *service) Call1(param string) error {
	return nil
}

func (s *service) Call2(param *string) error {
	return nil
}

func (s *service) Call3(*model.StructModel) error {
	return nil
}

func (s *service) Call4(model.StructModel) error {
	return nil
}
