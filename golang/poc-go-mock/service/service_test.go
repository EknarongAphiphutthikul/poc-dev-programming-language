package service_test

import (
	"go.uber.org/mock/gomock"
	mock_service "poc-go-mock/mock/service"
	"poc-go-mock/model"
	"testing"
)

func Test(t *testing.T) {
	t.Run("call1 param string test pass", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()

		service.EXPECT().Call1("test").Return(nil)

		err := service.Call1("test")
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call1 param string test fail", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()

		service.EXPECT().Call1("").Return(nil)

		err := service.Call1("test")
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})

	t.Run("call2 param nil", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()

		service.EXPECT().Call2(gomock.Any()).Return(nil)

		err := service.Call2(nil)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call2 param nil2", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()

		service.EXPECT().Call2(nil).Return(nil)

		err := service.Call2(nil)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call2 param same pointer", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		param := "test"

		service.EXPECT().Call2(&param).Return(nil)

		err := service.Call2(&param)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call2 param same pointer2", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		param := "test"

		service.EXPECT().Call2(nil).Return(nil)

		err := service.Call2(&param)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call2 param diff pointer", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		param := "test"
		param2 := "test"

		service.EXPECT().Call2(&param).Return(nil)

		err := service.Call2(&param2)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call2 param diff pointer2", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		param := "test"
		param2 := "test2"

		service.EXPECT().Call2(&param).Return(nil)

		err := service.Call2(&param2)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})

	t.Run("call3 param nil", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		param := model.StructModel{}

		service.EXPECT().Call3(gomock.Any()).Return(nil)

		err := service.Call3(&param)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call3 param nil2", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()

		service.EXPECT().Call3(nil).Return(nil)

		err := service.Call3(nil)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call3 param same pointer", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		param := model.StructModel{}

		service.EXPECT().Call3(&param).Return(nil)

		err := service.Call3(&param)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call3 param same pointer1", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		b := "test"
		param := model.StructModel{
			A: "test",
			B: &b,
			C: 10,
		}

		service.EXPECT().Call3(&param).Return(nil)

		err := service.Call3(&param)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})

	t.Run("call3 param diff pointer", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		param := model.StructModel{}
		param2 := model.StructModel{}

		service.EXPECT().Call3(&param).Return(nil)

		err := service.Call3(&param2)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call3 param diff pointer1", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		b := "test"
		param := model.StructModel{
			A: "test",
			B: &b,
			C: 10,
		}
		b1 := "test"
		param2 := model.StructModel{
			A: "test",
			B: &b1,
			C: 10,
		}

		service.EXPECT().Call3(&param).Return(nil)

		err := service.Call3(&param2)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})

	t.Run("call4 param", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		b := "test"
		param := model.StructModel{
			A: "test",
			B: &b,
			C: 10,
		}

		service.EXPECT().Call4(param).Return(nil)

		err := service.Call4(param)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call4 param2", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		b := "test"
		param := model.StructModel{
			A: "test",
			B: &b,
			C: 10,
		}
		b1 := "test"
		param2 := model.StructModel{
			A: "test",
			B: &b1,
			C: 10,
		}

		service.EXPECT().Call4(param).Return(nil)

		err := service.Call4(param2)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call4 param3", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		b := "test"
		param := model.StructModel{
			A: "test",
			B: &b,
			C: 10,
		}
		b1 := "test"
		param2 := model.StructModel{
			A: "test",
			B: &b1,
			C: 10,
		}

		service.EXPECT().Call4(&param).Return(nil)

		err := service.Call4(param2)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
	t.Run("call4 param4", func(t *testing.T) {
		ctrl, service := NewMockTest(t)
		defer ctrl.Finish()
		b := "test"
		param := model.StructModel{
			A: "test",
			B: &b,
			C: 9,
		}
		b1 := "test"
		param2 := model.StructModel{
			A: "test",
			B: &b1,
			C: 10,
		}

		service.EXPECT().Call4(param).Return(nil)

		err := service.Call4(param2)
		if err != nil {
			t.Errorf("error: %v", err)
		}
	})
}

func NewMockTest(t *testing.T) (*gomock.Controller, *mock_service.MockIService) {
	ctrl := gomock.NewController(t)
	service := mock_service.NewMockIService(ctrl)
	return ctrl, service
}
